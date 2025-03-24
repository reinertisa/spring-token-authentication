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
