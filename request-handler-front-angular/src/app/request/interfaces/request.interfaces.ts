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
  result: TisTableResult[];
}

export interface TisTableResult {
  columns: string[];
  data_types: string[];
  rows: Array<Array<any>>;
  rows_count: number;
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