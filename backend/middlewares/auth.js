const jwt = require("jsonwebtoken");
const { AppError } = require("../utils/errors");

exports.verifyToken = (req, res, next) => {
  try {
    const { FarmWiseKey } = req.cookies;
    if (!FarmWiseKey) throw new Error();

    const decoded = jwt.verify(FarmWiseKey, process.env.JWT_TOKEN_KEY);
    req.user = decoded;
    next();
  } catch (err) {
    throw new AppError("UNAUTHORIZED");
  }
};
