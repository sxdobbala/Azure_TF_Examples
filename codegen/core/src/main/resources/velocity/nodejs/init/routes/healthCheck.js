'use strict';

const express = require('express');
const router = express.Router();

const healthCheckApi = require('../api/v1/healthCheckApi');

/**
 * @swagger
 * /v1/healthCheck:
 *  get:
 *    tags:
 *      - HealthCheck
 *    description: Returns health check
 *    operationId: getHealth
 *    produces:
 *      - text/html
 *    responses:
 *      200:
 *        description: health check success
 */

router.get('', healthCheckApi.getHealth);

module.exports = router;
