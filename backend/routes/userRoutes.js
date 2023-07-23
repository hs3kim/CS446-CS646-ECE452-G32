const express = require("express");

const { getUserDetails } = require("../controllers/user");

const router = express.Router();

router.get("/get", getUserDetails);

module.exports = router;
