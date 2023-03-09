import React, {useEffect, useState} from "react";
import {Button, message} from "antd";
import { SendOutlined } from "@ant-design/icons";
import {useNavigate} from "react-router-dom";
import ScrollToBottom from "react-scroll-to-bottom";
import "./Chat.css";
import {
    getMyUserInfo,
    getUsers,
    countNewMessages,
    findChatMessage,
    findChatMessages
} from "../util/ApiUtil";


// Rsocket 0.0.27
import {
    RSocketClient
    , JsonSerializer
    , IdentitySerializer
} from "rsocket-core";
import RSocketWebSocketClient from 'rsocket-websocket-client';


const wsIp = 'ws://172.30.1.50:7000/rs'

const Chat = (props) => {

    const navigate = useNavigate();

    const [userInfo, setUserInfo] = useState([]);
    const [rsocketClient, setRSocketClient] = useState(null);
    const [text, setText] = useState("");
    const [contacts, setContacts] = useState([]);
    const [activeContact, setActiveContact] = useState([]);


    const [messages, setMessages] = useState([]);
    const [messageStream, setMessageStream] = useState();
    const [newMessage, setNewMessage] = useState([]);

    /**
     * 초기로딩
     */
    useEffect(() => {
        if (localStorage.getItem("accessToken") === null) {
            // navigate("/login");
        }
        loadUserInfo();
    }, []);
    /**
     * Token 유효성 검사 및 사용자 정보 조회
     */
    const loadUserInfo = () => {
        getMyUserInfo()
            .then((myInfo) => {
                setUserInfo(myInfo);
            })
            .catch((error) => {
                console.log(error);
            });
    };
    useEffect(() => {
        if(userInfo.id === undefined) return;
        connect()
        loadContacts();
    },[userInfo])


    /**
     * RSocket 커넥션 연결
     */
    const connect = ()=> {
        const client = new RSocketClient({
            serializers: {
                data: JsonSerializer,
                metadata: IdentitySerializer,
            },
            setup: {
                payload: {
                    data: userInfo.email
                },
                keepAlive: 60000,
                lifetime: 180000,
                dataMimeType: 'application/json',
                metadataMimeType: 'message/x.rsocket.routing.v0',
            },
            transport: new RSocketWebSocketClient({
                url: wsIp,
            })
        });
        client.connect().subscribe({
            onComplete : (socket)=>{
                console.log('소켓 연결됨')
                setRSocketClient(socket);
            },
            onError : (error) =>{
                console.log('e : ',error)
            },
        });
    }

    /**
     * Rsocket 커넥션 상태 확인 후
     * Stream 접속
     */
    useEffect(() => {
        if(rsocketClient == null) return
        requestStream()
    },[rsocketClient])
    /**
     * Stream 열고 나서
     */
    useEffect(() => {
        if( messageStream === undefined) return
        messageStream.request(2147483647);
        return ()=>{
            if(messageStream !== undefined) {
                console.log('스트림 종료')
                messageStream.cancel();
            }
            if(rsocketClient != null){
                console.log('소켓 종료')
                rsocketClient.close()
            }
        }
    },[messageStream])


    /**
     * Rsocket RequestStream 접슥을 위한 함수
     */
    const requestStream = ()=>{
        const stream = rsocketClient.requestStream({
            data : userInfo.id,
            metadata: String.fromCharCode('chat.message'.length) + 'chat.message',
        });
        stream.subscribe({
            onNext: (payload) => {
                setNewMessage(payload);
            },
            onError: (error) => {
                console.log("error : " ,error)
            },
            onComplete: () => {
                // console.log('Request stream completed');
            },
            onSubscribe: (subscription) => {
                setMessageStream(subscription);
            },
        });
    }

    /**
     * 대화상대 변경시마다 발생되는 이벤트
     */
    useEffect(() => {
        if (activeContact.id === undefined) return;
        findChatMessages(userInfo.id, activeContact.id)
        .then((msgs) => {
            setMessages(msgs);
        });
    }, [activeContact]);

    /**
     * RequestStream으로 부터 메세지가 수신되면 발생하는 이벤트
     */
    useEffect(() => {
        if(newMessage.length < 1)
            return;
        if(activeContact.id == newMessage.data.senderId) {
            onMessageRecevied(newMessage.data.id)
        }
        loadContacts()
    }, [newMessage]);

    /**
     * 신규 메세지 추가 함수
     */
    const onMessageRecevied = (notificationId)=>{
        findChatMessage(notificationId).then((msg)=>{
            setMessages([...messages, msg])
        })
    }


    const loadContacts = () => {
        const promise = getUsers().then((users) =>
            users.map((contact) =>
                countNewMessages(contact.id, userInfo.id).then((count) => {
                    contact.newMessages = count;
                    return contact;
                })
            )
        );
        promise.then((promises) =>
            Promise.all(promises).then((users) => {
                setContacts(users);
            })
        );
    };

    /**
     * 전송날짜 관련 유틸 함수
     */
    const dateFormat = (timestamp) =>{
        if(typeof timestamp == 'string') {
            const year = timestamp.substring(0, 4);
            const month = timestamp.substring(5, 7);
            const day = timestamp.substring(8, 10);
            let hours = Number(timestamp.substring(11, 13)) + 9;
            let ampm = hours >= 12 ? '오후' : '오전';
            hours = hours >= 12 ? hours - 12 : hours;
            hours = hours < 10 ? "0" + hours : hours;
            let minute = timestamp.substring(14, 16);
            return ampm + " " + hours + " : " + minute;
        } else{
            let hours = timestamp.getHours();
            let ampm = hours >= 12 ? '오후' : '오전';
            hours = hours >= 12 ? hours - 12 : hours;
            hours = hours < 10 ? "0" + hours : hours;
            let minute = timestamp.getMinutes();
            return ampm + " " + hours + " : " + minute;
        }
    }

    /**
     * Rsocket RequestResponse
     * 메세지 전송 함수
     */
    const send = (msg) => {
        if (activeContact.id == undefined) return;
        if( userInfo.id == null ) return
        if (msg.trim() == "") return;
        const message = {
            // senderId: myInfo.id,
            senderId: userInfo.id,
            receiverId: activeContact.id,
            senderName: userInfo.name,
            receiverName: activeContact.name,
            content: msg,
            timestamp: new Date(),
        };
        rsocketClient.requestResponse({
            data: message,
            metadata: String.fromCharCode('chat.sendMessage'.length) + 'chat.sendMessage',
        }).subscribe({
            onComplete: (msg) => {
            },
            onError: error => {
                console.log(error);
            },
            onSubscribe: subscription => {
                console.log('subscription :: ' , subscription)
                messages.push(message);
            },
        })

    }
    return (
        <div id="frame">
            <div id="sidepanel">
                <div id="profile">
                    <div className="wrap">
                        <img
                            id="profile-img"
                            className="online"
                            alt=""
                        />
                        <p>{userInfo.name}</p>
                        <div id="status-options">
                            <ul>
                                <li id="status-online" className="active">
                                    <span className="status-circle"></span> <p>Online</p>
                                </li>
                                <li id="status-away">
                                    <span className="status-circle"></span> <p>Away</p>
                                </li>
                                <li id="status-busy">
                                    <span className="status-circle"></span> <p>Busy</p>
                                </li>
                                <li id="status-offline">
                                    <span className="status-circle"></span> <p>Offline</p>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div id="search"/>
                <div id="contacts">
                    <ul>
                        {contacts.map((userList) =>
                            <li key={userList.id}
                                onClick={() => setActiveContact(userList)}
                                className={activeContact && userList.id === activeContact.id ? "contact active" : "contact"}
                            >
                                <div className="wrap">
                                    <span
                                        className={userList.connectStatus ? "contact-status online" : "contact-status offline"}></span>
                                    <div className="meta">
                                        <p className="name">{userList.name}</p>
                                        {userList.newMessages !== undefined &&
                                            userList.newMessages > 0 && (
                                                <p className="preview">
                                                    {userList.newMessages} new messages
                                                </p>
                                            )}
                                    </div>
                                </div>
                            </li>
                        )}
                    </ul>
                </div>
                <div id="bottom-bar">
                    <button id="addcontact" onClick={() => {
                        navigate("/");
                    }}>
                        <i className="fa fa-user fa-fw" aria-hidden="true"></i>{" "}
                        <span>Profile</span>
                    </button>
                    <button id="settings">
                        <i className="fa fa-cog fa-fw" aria-hidden="true"></i>{" "}
                        <span>Settings</span>
                    </button>
                </div>
            </div>
            <div className="content">
                <div className="contact-profile">
                    <img alt=""/>
                    <p>{activeContact == '' ? "대화 상대를 선택해주세요" : activeContact.name+ "님과 대화중"}</p>
                </div>
                <div>
                <ScrollToBottom className="messages" wrapperStyle={{ height: '100px' }}>
                    <ul>
                        {messages.map((msg,index) => (
                            <li key={index} className={msg.senderId === userInfo.id ? "sent" : "replies"}>
                                <p>{msg.content}</p>
                                <span>{ dateFormat(msg.timestamp)}</span>
                            </li>
                        ))}
                    </ul>
                </ScrollToBottom>
            </div>
                <div className="message-input">
                    <div className="wrap">
                        <input
                            name="user_input"
                            size="large"
                            placeholder="Write your message..."
                            value = {text}
                            onChange={(event) => setText(event.target.value)}
                            onKeyDown={(event) =>{
                                if(event.key === "Enter"){
                                    send(text);
                                    setText("");
                                }
                            }}
                        />
                        <Button className="" icon={<SendOutlined />}
                            onClick={()=> {
                                send(text);
                                setText("");
                            }}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Chat;
