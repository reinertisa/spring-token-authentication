import {Link, useLocation} from "react-router-dom";

export default function Verify() {
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const key = searchParams.get('key');

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

    if (location.pathname === '/verify/account') {
        return (
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-lg-6 col-md-6 col-sm-12" style={{marginTop: '100px'}}>
                        <div className="card">
                            <div className="card-body">
                                <div className="alert alert-dismissible alert-success">
                                    Account verified. You can log in now.
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
}