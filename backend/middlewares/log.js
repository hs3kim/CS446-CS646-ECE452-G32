exports.logRequests = (req, res, next) => {
  const msg = `Received a request: ${req.method} ${req.originalUrl} ${
    Object.keys(req.body).length !== 0
      ? `with body ${JSON.stringify(req.body)}`
      : ``
  }`;

  console.log({ msg });
  next();
};
