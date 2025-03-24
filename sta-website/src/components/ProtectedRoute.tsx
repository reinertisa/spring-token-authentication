import {Navigate, Outlet, useLocation} from "react-router-dom";
import {Key} from "../enum/cache.key.ts";

export default function ProtectedRoute() {

    const location = useLocation();
    const isLoggedIn: boolean = JSON.parse(localStorage.getItem(Key.LOGGEDIN)!) as boolean || false;

    if (isLoggedIn) {
        return <Outlet />
    } else {
        // Toast notification
        return <Navigate to={'/login'} replace={true} state={{from: location}} />
    }
}