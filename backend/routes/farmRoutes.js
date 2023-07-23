const express = require("express");

const { createFarm, editFarm, deleteFarm, enrollWorker } = require("../controllers/farm");

const router = express.Router();

router.post("/new", createFarm);
router.post("/remove", deleteFarm);
router.post("/edit", editFarm);
router.post("/enroll", enrollWorker);

module.exports = router;
