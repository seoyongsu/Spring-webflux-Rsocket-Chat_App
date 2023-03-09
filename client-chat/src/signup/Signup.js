import React, { useEffect, useState } from "react";
import { Form, Input, Button, notification } from "antd";
import { signup } from "../util/ApiUtil";
import "./Signup.css";
import {useNavigate} from "react-router-dom";


const Signup = (props) => {
    const navigate = useNavigate();

    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (localStorage.getItem("accessToken") !== null) {
            navigate("/");
        }
    }, []);

    const onFinish = (values) => {
        console.log(values)
        signup(values)
            .then((response) => {
                console.log("response : " , response)

                notification.success({
                    message: "Success",
                    description:
                        "Thank you! You're successfully registered. Please Login to continue!",
                });

                // props.history.push("/login");
                setLoading(false);
                navigate("/login");

            })
            .catch((error) => {
                console.log("e : ",error)
                notification.error({
                    message: "Error",
                    description:
                        error.message || "Sorry! Something went wrong. Please try again!",
                });
                setLoading(false);
            });
    }


    return (
        <div className="signup-container">
            <Form name="normal_signup" className="signup-form" initialValues={{ remember: true }} onFinish={onFinish}>

                <Form.Item name="email"
                   rules={[
                     {type: "email", message:"Please Validation Check email!"}
                    ,{required: true, message: "Please input your email!" }
                    ]}
                >
                    <Input size="large" placeholder="Email" />
                </Form.Item>

                <Form.Item name="name" rules={[{ required: true, message: "Please input your name!" }]}>
                    <Input size="large" placeholder="Name" />
                </Form.Item>


                <Form.Item name="password" rules={[{ required: true, message: "Please input your Password!" }]}>
                    <Input size="large" type="password" placeholder="Password" autoComplete="off"/>
                </Form.Item>

                <Form.Item>
                    <Button
                        shape="round"
                        size="large"
                        htmlType="submit"
                        className="login-form-button"
                        loading={loading}
                    >
                        Signup
                    </Button>
                </Form.Item>
                Already a member? <a href="/login">Log in</a>
            </Form>
        </div>
    );
};


export default Signup;
