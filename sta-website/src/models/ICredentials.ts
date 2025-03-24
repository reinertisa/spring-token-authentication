import {IUser} from "./IUser.ts";

export interface IUserRequest {
    email: string;
    password?: string;
}

export interface IRegisterRequest extends IUserRequest {
    firstName: string;
    lastName: string;
    phone?: string;
    bio?: string;
}

export type EmailAddress = Pick<IUserRequest, "email">;
export type UpdatePassword = Pick<IUserRequest, 'password'> & {newPassword: string, confirmedNewPassword: string};
export type UpdateNewPassword = Pick<IUser, "userId"> & UpdatePassword;