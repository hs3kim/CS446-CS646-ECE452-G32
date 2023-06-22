const { AppError, mapErrorStatusToStatusCode } = require("../utils/errors");
const { formatErrorResponse } = require("../utils/formatResponse");

exports.globalErrorHandler = (err, req, res, next) => {
  console.error(err);
  if (err instanceof AppError) {
    const statusCode = mapErrorStatusToStatusCode(err.errorStatus);
    res
      .status(statusCode)
      .json(formatErrorResponse(err.errorStatus, err.message));
  } else {
    res.status(500).json(formatErrorResponse("INTERNAL_SERVER_ERROR"));
  }
};
