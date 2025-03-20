import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import {setupStore} from "./store/store.ts";
import {createBrowserRouter, createRoutesFromElements, Route, RouterProvider} from "react-router-dom";
import Login from "./components/Login.tsx";
import {Provider} from "react-redux";

const store = setupStore();
const router = createBrowserRouter(createRoutesFromElements(
    <Route path="/" element={<App />}>
        <Route path="login" element={<Login />} />
    </Route>
));

const root = createRoot(document.getElementById('root')!);

root.render(
    <Provider store={store}>
        <RouterProvider router={router} />
    </Provider>
)
