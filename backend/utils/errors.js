class AppError extends Error {
  constructor(status, message) {
    super(message || "");

    this.errorStatus = status;

    Object.setPrototypeOf(this, new.target.prototype);
    Error.captureStackTrace(this);
  }
}

const mapErrorStatusToStatusCode = (status) => {
  return {
    NOT_FOUND: 404,
    UNAUTHORIZED: 401,
    BAD_REQUEST: 400,
    DUPLICATE_RECORD: 409,
    INTERNAL_SERVER_ERROR: 500,
    THIRD_PARTY_ERROR: 400,
  }[status];
};

module.exports = {
  AppError,
  mapErrorStatusToStatusCode,
};
