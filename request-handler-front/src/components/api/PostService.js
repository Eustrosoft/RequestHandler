import axios from "axios";

export default class PostService {

    static async login(body) {
        const response = await axios.post(`http://localhost:8080/eustrosofthandler_war/login`, body);
        return response;
    }

    static async logout() {
        const response = await axios.get(`http://localhost:8080/eustrosofthandler_war/logout`)
        return response;
    }
}