import {useLocation} from "react-router-dom";
import {IRegisterRequest} from "../models/ICredentials.ts";
import {z} from 'zod';
import {useForm} from 'react-hook-form';
import {zodResolver} from "@hookform/resolvers/zod";
import {Key} from "../enum/cache.key.ts";
import {userAPI} from "../services/UserService.ts";
import {useEffect} from "react";


const schema = z.object({
    firstName: z.string().min(1, 'First name is required'),
    lastName: z.string().min(1, 'Last name is rquired'),
    email: z.string().min(3, 'Email is required').email('Invalid email address'),
    password: z.string().min(5, 'Password is required')
});


export default function Register() {
    const location = useLocation();
    const isLoggedIn: boolean = JSON.parse(localStorage.getItem(Key.LOGGEDIN)!) as boolean || false;
    const [registerUser, {data, error, isLoading, isSuccess}] = userAPI.useRegisterUserMutation();

    const {register, handleSubmit, reset, formState, getFieldState} = useForm<IRegisterRequest>({
        resolver: zodResolver(schema),
        mode: 'onTouched'
    });

    const isFieldValid = (fieldName: keyof IRegisterRequest): boolean =>
        getFieldState(fieldName, formState).isTouched && !getFieldState(fieldName, formState).invalid;

    const handleRegister = async (registerRequest: IRegisterRequest) => await registerUser(registerRequest);

    useEffect(() => {
        reset();
    }, [isSuccess])

    return (
        <div>Register</div>
    )
}