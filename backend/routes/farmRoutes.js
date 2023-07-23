const express = require("express");

const { createFarm, enrollWorker } = require("../controllers/farm");

const router = express.Router();

router.post("/new", createFarm);
router.post("/enroll", enrollWorker);

module.exports = router;
