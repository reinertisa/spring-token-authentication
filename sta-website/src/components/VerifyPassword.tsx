import {Link, useLocation} from "react-router-dom";
import {userAPI} from "../services/UserService.ts";
import {useEffect} from "react";
import {IResponse} from "../models/IResponse.ts";

export default function VerifyPassword() {
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const key = searchParams.get('key');

    const [verifyPassword, {data, error, isLoading, isSuccess}] = userAPI.useVerifyPasswordMutation()

    useEffect(() => {
        if (key && location.pathname.includes('/verify/password')) {
            verifyPassword(key);
        }
    }, [])

    if (!key) {
        return (
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-lg-6 col-md-6 col-sm-12" style={{marginTop: '100px'}}>
                        <div className="card">
                            <div className="card-body">
                                <div className="alert alert-dismissible alert-danger">
                                    Invalid link. Please check the link and try again.
                                </div>
                                <hr className="my-3" />
                                <div className="row mb-3">
                                    <div className="col d-flex justify-content-start">
                                        <div className="btn btn-outline-light">
                                            <Link to="/login" style={{textDecoration: 'none'}}>Go to login</Link>
                                        </div>
                                    </div>
                                    <div className="col d-flex justify-content-end">
                                        <div className="link-dark">
                                            <Link to="/resetpassword">Forgot password?</Link>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }

    if (key && !isSuccess) {
        return (
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-lg-6 col-md-6 col-sm-12" style={{marginTop: '100px'}}>
                        <div className="card">
                            <div className="card-body">
                                {error && <div className="alert alert-dismissible alert-danger">
                                    {'data' in error ? (error.data as IResponse<void>).message! : 'An error occurred'}
                                </div>}
                                <div className="d-flex align-items-center">
                                    {!error && <>
                                        <strong role="status">Please wait. Verifying...</strong>
                                        <div className="spinner-border ms-auto" aria-hidden="true"></div>
                                    </>}
                                </div>
                                {error && <>
                                    <hr className="my-3" />
                                    <div className="row mb-3">
                                        <div className="col d-flex justify-content-start">
                                            <div className="btn btn-outline-light">
                                                <Link to="/login" style={{textDecoration: 'none'}}>Go to login</Link>
                                            </div>
                                        </div>
                                        <div className="col d-flex justify-content-end">
                                            <div className="link-dark">
                                                <Link to="/resetpassword">Forgot password?</Link>
                                            </div>
                                        </div>
                                    </div>
                                </>}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }

    if (isSuccess && location.pathname.includes('/verify/password')) {
        return (
            <div className="container">
                test
            </div>
        )
    }
}