import {useForm} from "react-hook-form";
import {IRegisterRequest, UpdatePassword} from "../../models/ICredentials.ts";
import {zodResolver} from "@hookform/resolvers/zod";
import {userAPI} from "../../services/UserService.ts";
import {z} from "zod";
import {useEffect} from "react";
import Loader from "./Loader.tsx";

const schema = z.object({
    newPassword: z.string().min(5, {message: 'New password required'}),
    confirmNewPassword: z.string().min(5, {message: 'Confirm password is required'}),
    password: z.string().min(5, {message: 'Password is required'})
}).superRefine(({newPassword, confirmNewPassword}, ctx) => {
    if (newPassword !== confirmNewPassword) {
        ctx.addIssue({
            code: z.ZodIssueCode.custom,
            path: ['confirmNewPassword'],
            message: 'New password and confirm password do not match'
        })
    }
})

export default function Password() {

    const {register, handleSubmit, reset, formState: form, getFieldState} = useForm<UpdatePassword>({
        resolver: zodResolver(schema),
        mode: 'onTouched'
    });
    const {data: user, error, isLoading, isSuccess, refetch} = userAPI.useFetchUserQuery();
    const [updatePassword, {data: updateData, isLoading: updateLoading, isSuccess: updateSuccess}] = userAPI.useUpdatePasswordMutation();
    const isFieldValid = (fieldName: keyof UpdatePassword): boolean =>
        getFieldState(fieldName, form).isTouched && !getFieldState(fieldName, form).invalid;

    const onUpdatePassword = async (request: UpdatePassword) => await updatePassword(request);

    useEffect(() => {
        reset();
    }, [updateSuccess]);

    return (
        <>
            {isLoading && <Loader />}
            {isSuccess && <>
                <h4 className="mb-3">Password</h4>
                <hr />
                <form onSubmit={handleSubmit(onUpdatePassword)} className="needs-validation" noValidate>
                    <div className="row g-3">
                        <div className="col-12">
                            <label htmlFor="password" className="form-label">Current Password</label>
                            <div className="input-group has-validation">
                                <span className="input-group-text"><i className="bi bi-key"></i></span>
                                <input
                                    type="password"
                                    {...register('password')}
                                    className={`form-control ' ${form.errors.password ? 'is-invalid' : ''} ${isFieldValid('password') ? 'is-valid' : ''}`}
                                    name="password"
                                    autoComplete="on"
                                    id="password"
                                    placeholder="Current Password"
                                />
                                <div className="invalid-feedback">{form.errors.password?.message}</div>
                            </div>
                        </div>
                        <hr className="my-4" />
                        <div className="col-12">
                            <label htmlFor="newPassword" className="form-label">New Password</label>
                            <div className="input-group has-validation">
                                <span className="input-group-text"><i className="bi bi-key"></i></span>
                                <input
                                    type="password"
                                    {...register('newPassword')}
                                    className={`form-control ' ${form.errors.newPassword ? 'is-invalid' : ''} ${isFieldValid('newPassword') ? 'is-valid' : ''}`}
                                    name="newPassword"
                                    autoComplete="on"
                                    id="newPassword"
                                    placeholder="New Password"
                                />
                                <div className="invalid-feedback">{form.errors.newPassword?.message}</div>
                            </div>
                        </div>
                        <div className="col-12">
                            <label htmlFor="confirmNewPassword" className="form-label">Confirm New Password</label>
                            <div className="input-group has-validation">
                                <span className="input-group-text"><i className="bi bi-key"></i></span>
                                <input
                                    type="password"
                                    {...register('confirmNewPassword')}
                                    className={`form-control ' ${form.errors.confirmNewPassword ? 'is-invalid' : ''} ${isFieldValid('confirmNewPassword') ? 'is-valid' : ''}`}
                                    name="confirmNewPassword"
                                    autoComplete="on"
                                    id="confirmNewPassword"
                                    placeholder="Confirm New Password"
                                />
                                <div className="invalid-feedback">{form.errors.confirmNewPassword?.message}</div>
                            </div>
                        </div>
                        <hr className="my-4" />
                        <div className="col">
                            <button disabled={!form.isValid || form.isSubmitting || isLoading || updateLoading || user?.data.user.role === 'USER'} className="btn btn-primary btn-block" type="submit">
                                {(form.isSubmitting || isLoading || updateLoading) && <span className="spinner-border spinner-border-sm" aria-hidden="true"></span>}
                                <span role="status">{(form.isSubmitting || isLoading || updateLoading) ? 'Loading...' : 'Update'}</span>
                            </button>
                        </div>
                    </div>
                </form>
            </>}
        </>
    )
}