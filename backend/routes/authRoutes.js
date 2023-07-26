const express = require("express");

const { registerUser, loginUser, changePassword } = require("../controllers/auth");

const router = express.Router();

router.post("/register", registerUser);
router.post("/login", loginUser);
router.post("/change-password", changePassword);

module.exports = router;
