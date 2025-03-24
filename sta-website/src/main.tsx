import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import {setupStore} from "./store/store.ts";
import {createBrowserRouter, createRoutesFromElements, Navigate, Route, RouterProvider} from "react-router-dom";
import Login from "./components/Login.tsx";
import {Provider} from "react-redux";
import NavBar from "./components/NavBar.tsx";
import Documents from "./components/document/Documents.tsx";
import Register from "./components/Register.tsx";
import VerifyAccount from "./components/VerifyAccount.tsx";
import ResetPassword from "./components/ResetPassword.tsx";
import VerifyPassword from "./components/VerifyPassword.tsx";

const store = setupStore();
const router = createBrowserRouter(createRoutesFromElements(
    <Route path="/" element={<App />}>
        <Route path="login" element={<Login />} />
        <Route path="register" element={<Register />} />
        <Route path="user/verify/account" element={<VerifyAccount />} />
        <Route path="user/verify/password" element={<VerifyPassword />} />
        <Route path="resetpassword" element={<ResetPassword />} />
        <Route element={<NavBar />}>
            <Route index path="/documents" element={<Documents />} />
            <Route path="/" element={<Navigate to={'/documents'} />} />
        </Route>
    </Route>
));

const root = createRoot(document.getElementById('root')!);

root.render(
    <Provider store={store}>
        <RouterProvider router={router} />
    </Provider>
)
