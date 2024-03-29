import BaseURL, { axiosClient } from './commons/BaseURL';


let isLoggedIn = () => {
    let item = localStorage.getItem("jwt");
    if(item != null) {
        let parsedItem = JSON.parse(item);
        let expirationTime = parsedItem.expirationTime;
        if(new Date().getTime() > expirationTime) {
            
        }
        let token = parsedItem.value;

    }

}


const signInURL = `${BaseURL}/auth/sign-in`;
const signOutURL = `${BaseURL}/auth/sign-out`;
const isAuthenticatedURL = `${BaseURL}/auth`
const normalAuthRegisterURL = `${BaseURL}/auth/register`;

let SignIn = async (credentials) => {
    let serverResponse;
    try {
        let payload = signInPayloadCreator(credentials);
        serverResponse = await axiosClient.post(signInURL, payload);
    } catch (error) {
        console.log(error);
        serverResponse = error;
    }
    return serverResponse;
}

let SignOut = async () => {
    let serverResponse;
    try {
        serverResponse = await axiosClient.get(signOutURL);
    } catch (error) {
        serverResponse = error;
    }
    return serverResponse;
}

let signInPayloadCreator = (payload) => {
    console.log("what is payload ? " + JSON.stringify(payload));
    return {
        emailOrUsername: payload.usernameEmail,
        password: payload.password
    }
}

let registerPayloadCreater = (payload) => {
    return {
        username: payload.username,
        email: payload.email,
        firstName: payload.firstName,
        lastName: payload.lastName,
        password: payload.password,
        confirmPassword: payload.confirmPassword
    }

}

let registerUserUsingForm = async (userDetailsPayload) => {
    console.log("payload received ", userDetailsPayload);
    let serverResponse;
    try {
        serverResponse = await axiosClient.post(normalAuthRegisterURL, userDetailsPayload);
        console.log("I have received the response for registering the user api " + serverResponse.data);
    } catch (error) {
        console.log("I got an error ", error.response);
        serverResponse = error;
    }
    return serverResponse;
}

let isAuthenticatedCheck = () => {

}

export { SignIn, SignOut, registerUserUsingForm };

