export interface TisResponse {
  qtisver: number;
  responses: TisResponseBody[];
  qtisend: boolean;
}

export interface TisQuery {
  qtisver: number;
  requests: SqlQuery[] | FileQuery[];
  qtisend: boolean;
}

export interface TisResponseBody {
  subsystem: string;
  status: number;
  qid: number;
  err_code: number;
  err_msg: string;
  result: any;
}

export interface SqlQuery {
  subsystem: string;
  request: string;
  parameters: {
    method: string;
    query: string;
  };
}

export interface FileQuery {
  subsystem: string;
  request: string;
  parameters: {
    method: string;
    data: {
      file: string;
      name: string;
      ext: string;
    };
  };
}
