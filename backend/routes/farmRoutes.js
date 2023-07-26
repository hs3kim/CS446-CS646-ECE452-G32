const express = require("express");

const {
  createFarm,
  editFarm,
  deleteFarm,
  enrollWorker,
  unenrollWorker,
} = require("../controllers/farm");

const router = express.Router();

router.post("/new", createFarm);
router.post("/remove", deleteFarm);
router.post("/edit", editFarm);
router.post("/enroll", enrollWorker);
router.post("/unenroll", unenrollWorker);

module.exports = router;
