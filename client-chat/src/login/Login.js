import React, { useEffect, useState } from "react";
import { Form, Input, Button, Divider, notification } from "antd";
import {
    UserOutlined,
    LockOutlined,
} from "@ant-design/icons";

import {
    login,
    getSocialImage,
    getSocialLoginUrl,

} from "../util/ApiUtil";
import "./Login.css";
import {useNavigate} from "react-router-dom";


const Login = (props)=> {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [test, setTest] = useState(localStorage.getItem("accessToken"));

    useEffect(() => {

        console.log("accessToken : " , localStorage.getItem("accessToken"));
        if (localStorage.getItem("accessToken") !== null) {
            // navigate("/");
        }

        //Oauth2 QueryParam 임시용
        const queryParam = new URLSearchParams(window.location.search);
        const result = queryParam.get("result");
        if (result =='true') {
            const accessToken = queryParam.get("accessToken");
            localStorage.setItem("accessToken", accessToken);
            console.log('accessToken  , ', accessToken)
            navigate("/")
        }else{
            console.log('인증결과 false')
        }

    }, []);


    const onFinish = (values) => {
        setLoading(true);

        login(values)
            .then((response) => {
                console.log("response:: ", response.accessToken)
                localStorage.setItem("accessToken", response.accessToken);
                navigate("/");
                setLoading(false);
            })
            .catch((error) => {
                if (error.success === false) {
                    notification.error({
                        message: "Error",
                        description: "Email or Password is incorrect. Please try again!",
                    });
                } else {
                    notification.error({
                        message: "Error",
                        description:
                            error.message || "Sorry! Something went wrong. Please try again!",
                    });
                }
                setLoading(false);
            });
    };



    return (
        <div className="login-container">
            <Form name="normal_login" className="login-form"  initialValues={{ remember: true }}
                onFinish={onFinish}
            >
                <Form.Item name="username"
                           rules={[
                                {type: "email", message:"Please Validation Check email!"}
                               ,{ required: true, message: "Please input your email!" }
                           ]}
                >
                    <Input size="large" prefix={<UserOutlined className="site-form-item-icon" />} placeholder="Email"/>
                </Form.Item>
                <Form.Item name="password" rules={[{ required: true, message: "Please input your Password!" }]}>
                    <Input size="large"  prefix={<LockOutlined className="site-form-item-icon"/>}
                        type="password" placeholder="Password" autoComplete="off"
                    />
                </Form.Item>
                <Form.Item>
                    <Button shape="round" size="large" htmlType="submit" className="login-form-button" loading={loading}>
                        LOGIN
                    </Button>
                </Form.Item>
                <Divider>OR</Divider>
                <div className='socal_login_container'>
                    <a href={getSocialLoginUrl('kakao')}>
                        <img src={getSocialImage('kakao')}/>
                    </a>
                    <a href={getSocialLoginUrl('naver')}>
                        <img src={getSocialImage('naver')}/>
                    </a>
                    <a href={getSocialLoginUrl('google')}>
                        <img src={getSocialImage('google')}/>
                    </a>

                </div>
                <h3>
                    Not a member yet? <a href="/signup">Sign up</a>
                </h3>
            </Form>
        </div>
    );
}

export default Login;