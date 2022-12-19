import PostService from "./PostService";

export default class LoginService {

    static async login(body) {
        return await PostService.post('login', body);
    }

    static async logout() {
        return await PostService.post('logout', '');
    }
}