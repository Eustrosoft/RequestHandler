export interface TisQuery {
  qtisver: number;
  requests: SqlQuery[] | FileQuery[];
  qtisend: boolean;
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

// const json = {
//   subsystem: 'sql',
//   request: 'sql',
//   parameters: {
//     method: '',
//     query: 'select * from tis.v_users;',
//   },
// };

// const json2 = {
//   subsystem: 'file',
//   request: 'create',
//   parameters: {
//     method: '',
//     data: 'asd',
//   },
// };
