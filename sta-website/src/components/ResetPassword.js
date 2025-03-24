import {Navigate, useLocation} from "react-router-dom";
import {Key} from "../enum/cache.key.js";
import {useForm} from "react-hook-form";
import {EmailAddress} from "../models/ICredentials.js";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";


const schema = z.object({
    email: z.string().min(3, 'Email is required').email('Invalid email address'),
});

export default function ResetPassword() {
    const location = useLocation();
    const isLoggedIn: boolean = JSON.parse(localStorage.getItem(Key.LOGGEDIN)!) as boolean || false;
    const {register, handleSubmit, formState, getFieldState} = useForm<EmailAddress>({
        resolver: zodResolver(schema),
        mode: 'onTouched'
    });

    const isFieldValid = (fieldName: keyof EmailAddress): boolean => getFieldState(fieldName, formState).isTouched && !getFieldState(fieldName, formState).invalid;


    if (isLoggedIn) {
        return location?.state?.from?.pathname
            ? <Navigate to={location?.state?.from?.pathname} replace />
            : <Navigate to={'/'} replace />
    }

    return (
        <div>Reset Password</div>
    )
}