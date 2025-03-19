export interface IUser {
    id: number;
    userId: string;
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    bio: string;
    qrCodeImageUri?: string;
    imageUrl: string;
    accountNonExpired: boolean;
    accountNonLocked: boolean;
    credentialsNonExpired: boolean;
    lastLogin: string | number | Date;
    enabled: boolean;
    mfa: boolean;
    createdAt: string;
    updatedAt: string;
    createdBy: number;
    updatedBy: number;
    role: string;
    authorities: string;
}

export type Role = {
    role: string
};

export type User = {
    user: IUser
};

export type Users = {
    users: IUser[]
};