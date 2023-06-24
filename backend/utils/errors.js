class AppError extends Error {
  constructor(status, message) {
    super(message || "");

    this.errorStatus = status;

    Object.setPrototypeOf(this, new.target.prototype);
    Error.captureStackTrace(this);
  }
}

const mapErrorStatusToStatusCode = (status) => {
  switch (status) {
    case "BAD_REQUEST":
    case "THIRD_PARTY_ERROR":
      return 400;
    case "UNAUTHORIZED":
      return 401;
    case "NOT_FOUND":
      return 404;
    case "DUPLICATE_RECORD":
      return 409;
    default:
      return 500;
  }
};

module.exports = {
  AppError,
  mapErrorStatusToStatusCode,
};
