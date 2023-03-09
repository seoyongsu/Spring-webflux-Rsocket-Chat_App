import React, { useEffect, useState } from "react";
import { Card, Avatar } from "antd";
import { LogoutOutlined, WechatOutlined } from "@ant-design/icons";
import { getMyUserInfo } from "../util/ApiUtil";
import { useNavigate } from "react-router-dom";
import "./Profile.css";
const { Meta } = Card;

const Profile = (props) => {
    const navigate = useNavigate();
    const [userInfo, setUserInfo] = useState([]);
    const token = localStorage.getItem("accessToken");
    useEffect(() => {
        if (token === null) {
            console.log("null AccessToken");
            navigate("/login");
        }
        loadUserInfo();
    }, []);

    const loadUserInfo = () => {
        getMyUserInfo()
            .then((response) => {
                setUserInfo(response);
            })
            .catch((error) => {
                console.log(error);
            });
    };


    const logout = () => {
        localStorage.removeItem("accessToken");
        navigate("/login");
    };

    return (
        <div className="profile-container">
            <Card style={{ width: 420, border: "1px solid #e1e0e0" }}
                  actions={[
                      <WechatOutlined onClick={()=> navigate("/chat")}/>
                      ,<LogoutOutlined onClick={logout} />
                  ]}
             >
                <Meta
                    avatar={
                        <Avatar
                            className="user-avatar-circle"
                        />
                    }
                    title={userInfo.name}
                    description={userInfo.email}
                />
            </Card>
        </div>
    );
};

export default Profile;
