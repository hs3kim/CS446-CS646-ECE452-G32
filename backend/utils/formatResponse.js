/*

All responses will be in JSON and will have format of 

{
    status: 
    statusMsg:
    data:
}

*/

exports.formatSuccessResponse = (data) => {
  return {
    status: "SUCCESS",
    statusMsg: "",
    data,
  };
};

exports.formatErrorResponse = (status, statusMsg = "") => {
  return {
    status,
    statusMsg,
    data: null,
  };
};
