-- ----------------------------
-- Table structure for tab_cdr
-- ----------------------------
DROP TABLE IF EXISTS `tab_cdr`;
CREATE TABLE `tab_cdr` (
  `org_msisdn` varchar(20) DEFAULT NULL,
  `dst_msisdn` varchar(20) DEFAULT NULL,
  `call_type` varchar(20) DEFAULT NULL,
  `org_ac` varchar(20) DEFAULT NULL,
  `visit_ac` varchar(20) DEFAULT NULL,
  `dst_ac` varchar(20) DEFAULT NULL,
  `roam_type` varchar(20) DEFAULT NULL,
  `long_type` varchar(20) DEFAULT NULL,
  `charge_rule` varchar(100) DEFAULT NULL,
  `dt` varchar(20) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `fee` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;




#如下是redis 操作
hset "ac_local" "130" "000"
hset "ac_local" "131" "010"
hset "ac_local" "132" "020"
hset "ac_local" "133" "030"
hset "ac_local" "134" "040"
hset "ac_local" "135" "050"

hset "ac_other" "136" "060"
hset "ac_other" "137" "070"
hset "ac_other" "138" "080"
hset "ac_other" "139" "090"
