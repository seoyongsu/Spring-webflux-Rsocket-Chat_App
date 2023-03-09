import React from "react";
import {
    BrowserRouter,
    Routes,
    Route,
} from "react-router-dom";

import Profile from "./profile/Profile"
import Login from "./login/Login"
import Signup from "./signup/Signup"
import Chat from "./chat/Chat"


const App = (props) => {
    return (
        <div className="App">
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Profile />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/signup" element={<Signup />} />
                    <Route path="/chat" element={<Chat />} />
                </Routes>
            </BrowserRouter>
        </div>
    );
}
export default App;
