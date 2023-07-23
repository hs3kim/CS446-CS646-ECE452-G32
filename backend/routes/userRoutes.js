const express = require("express");

const { getUserDetails, editUser } = require("../controllers/user");

const router = express.Router();

router.get("/get", getUserDetails);
router.post("/update", editUser);

module.exports = router;
