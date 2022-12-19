import PostService from "./PostService";

export default class RequestHandlerService {

    static async process(body) {
        return await PostService.post('dispatch', body);
    }
}