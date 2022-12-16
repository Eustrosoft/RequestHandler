import axios from "axios";
import {APP_URL} from "../Constants";

export default class LoginService {

    static async login(body) {
        return await fetch(`${APP_URL}login`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        })
    }

    static async logout() {
        return await axios.post(`${APP_URL}logout`)
    }
}