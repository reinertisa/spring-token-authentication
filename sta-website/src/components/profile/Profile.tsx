import {userAPI} from "../../services/UserService.ts";
import {IRegisterRequest} from "../../models/ICredentials.ts";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import Loader from "./Loader.tsx";


const schema = z.object({
    firstName: z.string().min(1, 'First name is required'),
    lastName: z.string().min(1, 'Last name is required'),
    email: z.string().min(3, 'Email is required').email('Invalid email address'),
    password: z.string().min(5, 'Password is required'),
    bio: z.string().min(5, 'Bio is required')
});

export default function Profile() {

    const {register, handleSubmit, formState: form, getFieldState} = useForm<IRegisterRequest>({
        resolver: zodResolver(schema),
        mode: 'onTouched'
    });
    const {data: user, error, isLoading, isSuccess, refetch} = userAPI.useFetchUserQuery();
    const [update, {data: updateData, isLoading: updateLoading}] = userAPI.useUpdateUserMutation();

    const updateUser = async (user: IRegisterRequest) => await update(user);

    const isFieldValid = (fieldName: keyof IRegisterRequest): boolean =>
        getFieldState(fieldName, form).isTouched && !getFieldState(fieldName, form).invalid;

    return (
        <>
            {isLoading && <Loader />}
            {isSuccess && <>
                <h4 className="mb-3">Profile</h4>
                <hr />
                <form onSubmit={handleSubmit(updateUser)} className="needs-validation" noValidate>
                    <div className="row g-3">
                        <div className="col-sm-6">
                            <label htmlFor="firstName" className="form-label">First name</label>
                            <div className="input-group has-validation">
                                <span className="input-group-text"><i className="bi bi-person-vcard"></i></span>
                                <input
                                    type="text"
                                    {...register('firstName')}
                                    name="firstName"
                                    className={`form-control ' ${form.errors.firstName ? 'is-invalid' : ''} ${isFieldValid('firstName') ? 'is-valid' : ''}`}
                                    id="firstName"
                                    placeholder="First name"
                                    required={true}
                                    disabled={user?.data.user.role === 'USER'}
                                    defaultValue={user?.data.user.firstName}
                                />
                                <div className="invalid-feedback">{form.errors.firstName?.message}</div>
                            </div>
                        </div>
                        <div className="col-sm-6">
                            <label htmlFor="lastName" className="form-label">Last name</label>
                            <div className="input-group has-validation">
                                <span className="input-group-text"><i className="bi bi-person-vcard"></i></span>
                                <input
                                    type="text"
                                    {...register('lastName')}
                                    name="lastName"
                                    className={`form-control ' ${form.errors.lastName ? 'is-invalid' : ''} ${isFieldValid('lastName') ? 'is-valid' : ''}`}
                                    id="lastName"
                                    placeholder="Last name"
                                    required={true}
                                    disabled={user?.data.user.role === 'USER'}
                                    defaultValue={user?.data.user.lastName}
                                />
                                <div className="invalid-feedback">{form.errors.lastName?.message}</div>
                            </div>
                        </div>
                        <div className="col-12">
                            <label htmlFor="email" className="form-label">Email address</label>
                            <div className="input-group has-validation">
                                <span className="input-group-text"><i className="bi bi-envelope"></i></span>
                                <input
                                    type="text"
                                    {...register('email')}
                                    name="email"
                                    className={`form-control ' ${form.errors.email ? 'is-invalid' : ''} ${isFieldValid('email') ? 'is-valid' : ''}`}
                                    id="email"
                                    placeholder="Email address"
                                    required={true}
                                    disabled={user?.data.user.role === 'USER'}
                                    defaultValue={user?.data.user.email}
                                />
                                <div className="invalid-feedback">{form.errors.email?.message}</div>
                            </div>
                        </div>
                        <div className="col-12">
                            <label htmlFor="phone" className="form-label">Phone number</label>
                            <div className="input-group has-validation">
                                <span className="input-group-text"><i className="bi bi-telephone"></i></span>
                                <input
                                    type="text"
                                    {...register('phone')}
                                    name="email"
                                    className={`form-control ' ${form.errors.phone ? 'is-invalid' : ''} ${isFieldValid('phone') ? 'is-valid' : ''}`}
                                    id="phone"
                                    placeholder="123-456-7890"
                                    required={true}
                                    disabled={user?.data.user.role === 'USER'}
                                    defaultValue={user?.data.user.phone}
                                />
                                <div className="invalid-feedback">{form.errors.phone?.message}</div>
                            </div>
                        </div>
                        <div className="col-12">
                            <label htmlFor="bio" className="form-label">Bio</label>
                            <textarea
                                {...register('bio')}
                                name="bio"
                                className={`form-control ' ${form.errors.bio ? 'is-invalid' : ''} ${isFieldValid('bio') ? 'is-valid' : ''}`}
                                id="bio"
                                placeholder="Something about yourself here"
                                required={true}
                                disabled={user?.data.user.role === 'USER'}
                                defaultValue={user?.data.user.bio}
                                rows={3}
                            ></textarea>
                            <div className="invalid-feedback">{form.errors.bio?.message}</div>
                        </div>
                    </div>
                    <hr className="my-4" />
                    <div className="col">
                        <button disabled={!form.isValid || form.isSubmitting || isLoading || updateLoading || user?.data.user.role === 'USER'} className="btn btn-primary btn-block" type="submit">
                            {(form.isSubmitting || isLoading || updateLoading) && <span className="spinner-border spinner-border-sm" aria-hidden="true"></span>}
                            <span role="status">{(form.isSubmitting || isLoading || updateLoading) ? 'Loadin...' : 'Update'}</span>
                        </button>
                    </div>
                </form>
            </>}
        </>
    )
}