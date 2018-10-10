SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists link_admin_role]zdw;
CREATE TABLE `LINK_ADMIN_ROLE` (
  `ADMIN_ID` INT(11) NOT NULL,
  `ROLE_ID` INT(11) NOT NULL,
  PRIMARY KEY (`ADMIN_ID`,`ROLE_ID`),
  KEY `FK_ITN82B1MYDNKM9XEHOC59SUO8` (`ROLE_ID`),
  KEY `FK_RBWBQRAHBSI53IGQPPBGJX74W` (`ADMIN_ID`),
  CONSTRAINT `FK_ITN82B1MYDNKM9XEHOC59SUO8` FOREIGN KEY (`ROLE_ID`) REFERENCES `LZZ_ROLE` (`ROLEID`),
  CONSTRAINT `FK_RBWBQRAHBSI53IGQPPBGJX74W` FOREIGN KEY (`ADMIN_ID`) REFERENCES `LZZ_ADMININFO` (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists link_role_right]zdw;
CREATE TABLE `LINK_ROLE_RIGHT` (
  `RIGHT_ID` INT(11) NOT NULL,
  `ROLE_ID` INT(11) NOT NULL,
  PRIMARY KEY (`ROLE_ID`,`RIGHT_ID`),
  KEY `FK_N5PJRWMSL2P4VH8GUM555LPXJ` (`ROLE_ID`),
  KEY `FK_6DE87KA2CLTEJGPB63U2XA1RR` (`RIGHT_ID`),
  CONSTRAINT `FK_6DE87KA2CLTEJGPB63U2XA1RR` FOREIGN KEY (`RIGHT_ID`) REFERENCES `LZZ_RIGHT` (`RIGHTID`),
  CONSTRAINT `FK_N5PJRWMSL2P4VH8GUM555LPXJ` FOREIGN KEY (`ROLE_ID`) REFERENCES `LZZ_ROLE` (`ROLEID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_addforarticle]zdw;
CREATE TABLE `LZZ_ADDFORARTICLE` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `MAINBODY` LONGTEXT,
  `COMM_ID` INT(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_admininfo]zdw;
CREATE TABLE `LZZ_ADMININFO` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `USERNAME` VARCHAR(30) DEFAULT NULL,
  `REALNAME` VARCHAR(30) DEFAULT NULL,
  `PWORD` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_author]zdw;
CREATE TABLE `LZZ_AUTHOR` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `AUTHOR_NAME` VARCHAR(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_backup]zdw;
CREATE TABLE `LZZ_BACKUP` (
  `BACKUP_ID` TINYINT(4) NOT NULL AUTO_INCREMENT,
  `BACKUP_NAME` VARCHAR(100) NOT NULL,
  `BACKUP_DATE` DATETIME NOT NULL,
  `BACKUP_USER` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`BACKUP_ID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_channelinfo]zdw;
CREATE TABLE `LZZ_CHANNELINFO` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `CHANNELNAME` VARCHAR(30) DEFAULT NULL,
  `COMMONTABLE` VARCHAR(60) DEFAULT NULL,
  `ADDITIONALTABLE` VARCHAR(60) DEFAULT NULL,
  `ENNAME` VARCHAR(60) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_columninfo]zdw;
CREATE TABLE `LZZ_COLUMNINFO` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `NAME` VARCHAR(30) DEFAULT NULL,
  `CHANNEL_ID` INT(11) DEFAULT NULL,
  `PARENTID` INT(11) DEFAULT NULL,
  `ORDERNO` INT(11) DEFAULT NULL,
  `HTMLDIR` VARCHAR(100) DEFAULT NULL,
  `CLNTYPE` VARCHAR(30) DEFAULT NULL,
  `OUTLINK` VARCHAR(100) DEFAULT NULL,
  `CONTENTTPLNAME` VARCHAR(100) DEFAULT NULL,
  `CLNTITLE` VARCHAR(100) DEFAULT NULL,
  `CLNKEYWORDS` VARCHAR(200) DEFAULT NULL,
  `CLNDESC` VARCHAR(400) DEFAULT NULL,
  `SINGLECONTENT` LONGTEXT,
  `MYTPL` VARCHAR(100) DEFAULT NULL,
  `PERSONALSTYLE` VARCHAR(150) DEFAULT NULL,
  `COMM_HTML_ISUPDATED` char(5) DEFAULT 'no' COMMENT 'no:本栏目html未更新 yes:本栏目html已经被更新过了',
  PRIMARY KEY (`ID`),
  KEY `FK_HEWFYQG3B78WCXBY7XL7M71ED` (`CHANNEL_ID`),
  KEY `FK_DHKCKLV2FNPPCSRQMNP1RCNYJ` (`PARENTID`),
  CONSTRAINT `FK_DHKCKLV2FNPPCSRQMNP1RCNYJ` FOREIGN KEY (`PARENTID`) REFERENCES `LZZ_COLUMNINFO` (`ID`),
  CONSTRAINT `FK_HEWFYQG3B78WCXBY7XL7M71ED` FOREIGN KEY (`CHANNEL_ID`) REFERENCES `LZZ_CHANNELINFO` (`ID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_columntype]zdw;
CREATE TABLE `LZZ_COLUMNTYPE` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `TYPENAME` VARCHAR(30) DEFAULT NULL,
  `ENNAME` VARCHAR(30) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_commoncontent]zdw;
CREATE TABLE `LZZ_COMMONCONTENT` (
  `COMM_ID` INT(11) NOT NULL AUTO_INCREMENT,
  `COMM_TITLE` VARCHAR(150) DEFAULT NULL,
  `COMM_SHORTTITLE` VARCHAR(100) DEFAULT NULL,
  `COMM_CLICK` INT(11) DEFAULT '100',
  `COMM_AUTHOR` VARCHAR(30) DEFAULT NULL,
  `COMM_PUBLISHDATE` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `COMM_KEYWORDS` VARCHAR(200) DEFAULT NULL,
  `COMM_DESC` VARCHAR(200) DEFAULT NULL,
  `COMM_SRC` VARCHAR(30) DEFAULT NULL,
  `COMM_THUMBPIC` VARCHAR(100) DEFAULT '/UPLOADS/THUMB/',
  `COMM_DEFINEFLAG` VARCHAR(10) DEFAULT NULL,
  `COMM_INTRO` VARCHAR(250) DEFAULT NULL,
  `COMM_HTMLPATH` VARCHAR(250) DEFAULT '',
  `COLUMN_ID` INT(11) DEFAULT NULL,
  `COMM_MODIFYDATE` VARCHAR(50) DEFAULT NULL,
  `COMM_HTML_ISUPDATED` char(5) DEFAULT 'no' COMMENT 'no:本篇文档html未更新 yes:本篇文档html已经被更新过了',
  `COMM_SRC_URL` varchar(300) DEFAULT NULL COMMENT '爬虫爬取时设置,来源url',
  PRIMARY KEY (`COMM_ID`),
  KEY `FK_F7JYRTYCVNQB39PWEEKDYRGE1` (`COLUMN_ID`),
  CONSTRAINT `FK_F7JYRTYCVNQB39PWEEKDYRGE1` FOREIGN KEY (`COLUMN_ID`) REFERENCES `LZZ_COLUMNINFO` (`ID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_define_flag]zdw;
CREATE TABLE `LZZ_DEFINE_FLAG` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `DEFINE_FLAG` VARCHAR(100) NOT NULL DEFAULT '',
  `EN_NAME` VARCHAR(6) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_download]zdw;
CREATE TABLE `LZZ_DOWNLOAD` (
  `DOWNDATE` DATETIME NOT NULL,
  `SOFTNAME` VARCHAR(255) NOT NULL,
  `IP` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`DOWNDATE`,`SOFTNAME`,`IP`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_execlog]zdw;
CREATE TABLE `LZZ_EXECLOG` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `EXECDATE` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ADMIN_ID` INT(11) NOT NULL,
  `EXECURLDESC` VARCHAR(255) DEFAULT NULL,
  `EXECTYPE` VARCHAR(255) NOT NULL,
  `EXECURL` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_IWGQQ7CTG2LD8BNH4P9KLP4I0` (`ADMIN_ID`),
  CONSTRAINT `FK_IWGQQ7CTG2LD8BNH4P9KLP4I0` FOREIGN KEY (`ADMIN_ID`) REFERENCES `LZZ_ADMININFO` (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_extracolumnsdescforchl]zdw;
CREATE TABLE `LZZ_EXTRACOLUMNSDESCFORCHL` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `COLNAME` VARCHAR(30) DEFAULT NULL,
  `COLTYPE` VARCHAR(30) DEFAULT NULL,
  `SHOWTIP` VARCHAR(30) DEFAULT NULL,
  `ADDITIONALTABLE` VARCHAR(30) DEFAULT NULL,
  `ALLOWNULL` TINYINT(1) DEFAULT NULL,
  `DEFAULTVAL` VARCHAR(30) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_friendlink]zdw;
CREATE TABLE `LZZ_FRIENDLINK` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `LINKDESC` VARCHAR(255) DEFAULT NULL,
  `TYPE` TINYINT(4) DEFAULT NULL,
  `URL` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_right]zdw;
CREATE TABLE `LZZ_RIGHT` (
  `RIGHTID` INT(11) NOT NULL AUTO_INCREMENT,
  `RIGHTNAME` VARCHAR(30) DEFAULT NULL,
  `CANASSIGN` VARCHAR(60) DEFAULT NULL,
  `RIGHTURL` VARCHAR(150) DEFAULT NULL,
  `PARENT_ID` INT(11) DEFAULT NULL,
  `RIGHTGROUP` INT(11) DEFAULT NULL,
  `RIGHTCODE` BIGINT(20) DEFAULT NULL,
  `COMMON` TINYINT(1) DEFAULT NULL,
  `RIGHTTYPE` VARCHAR(255) DEFAULT NULL,
  `ORDERNO` INT(11) DEFAULT '1',
  PRIMARY KEY (`RIGHTID`),
  KEY `FK_S6HBSPAC6CPEAM6804OKRJG7C` (`PARENT_ID`),
  CONSTRAINT `FK_S6HBSPAC6CPEAM6804OKRJG7C` FOREIGN KEY (`PARENT_ID`) REFERENCES `LZZ_RIGHT` (`RIGHTID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_role]zdw;
CREATE TABLE `LZZ_ROLE` (
  `ROLEID` INT(11) NOT NULL AUTO_INCREMENT,
  `ROLENAME` VARCHAR(50) NOT NULL,
  `ROLEVALUE` INT(11) NOT NULL,
  `ROLEDESC` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`ROLEID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_searchword]zdw;
CREATE TABLE `LZZ_SEARCHWORD` (
  `SEARCHDATE` DATE NOT NULL,
  `SEARCHTEXT` VARCHAR(255) NOT NULL,
  `SEARCHCOUNT` INT(11) DEFAULT NULL
) ENGINE=INNODB DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_source]zdw;
CREATE TABLE `LZZ_SOURCE` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `COME_FROM` VARCHAR(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_spider_cfg]zdw;
CREATE TABLE `LZZ_SPIDER_CFG` (
  `ID` VARCHAR(100) DEFAULT NULL,
  `COLUMN_ID` INT(11) DEFAULT NULL,
  `COL_NAME` VARCHAR(100) DEFAULT NULL,
  `SHOWTIP` VARCHAR(250) DEFAULT NULL,
  `ADDFORTB` VARCHAR(100) DEFAULT NULL,
  `IS_COMMON` TINYINT(4) DEFAULT NULL,
  `WEBSITE` VARCHAR(250) DEFAULT NULL
) ENGINE=INNODB DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_systemparam]zdw;
CREATE TABLE `LZZ_SYSTEMPARAM` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `PARAMNAME` VARCHAR(150) DEFAULT NULL,
  `PARAMVALUE` VARCHAR(600) DEFAULT NULL,
  `GROUPNAME` VARCHAR(150) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_uploadfile_info]zdw;
CREATE TABLE `LZZ_UPLOADFILE_INFO` (
  `UPLOAD_ID` INT(11) NOT NULL AUTO_INCREMENT,
  `ORIGINAL_NAME` VARCHAR(150) NOT NULL,
  `FILE_SIZE` BIGINT(20) NOT NULL,
  `FILE_TYPE` VARCHAR(20) NOT NULL,
  `WIDTH` VARCHAR(100) DEFAULT NULL,
  `HEIGHT` VARCHAR(100) DEFAULT NULL,
  `TIME_LONG` VARCHAR(100) DEFAULT NULL,
  `CONT_ID` BIGINT(20) NOT NULL,
  `FILE_PATH` VARCHAR(300) NOT NULL,
  PRIMARY KEY (`UPLOAD_ID`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8]zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_comment]zdw;
CREATE TABLE `lzz_comment` (
  `id` bigint(24) NOT NULL AUTO_INCREMENT COMMENT '标识',
  `comm_id` bigint(24) DEFAULT NULL COMMENT '文档id',
  `comment_cont` longtext,
  `pub_name` varchar(100) DEFAULT NULL,
  `pub_ip` varchar(50) DEFAULT NULL,
  `pub_location` varchar(200) DEFAULT NULL,
  `ding_cnt` bigint(24) DEFAULT '0',
  `cai_cnt` bigint(24) DEFAULT '0',
  `reply_id` bigint(24) DEFAULT NULL COMMENT '哪个评论的跟评',
  `create_time` datetime DEFAULT NULL,
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(32) NOT NULL DEFAULT 'system' COMMENT '创建者',
  `modifier` varchar(32) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `is_deleted` char(1) NOT NULL DEFAULT 'N' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='评论表']zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_crawl]zdw;
CREATE TABLE `lzz_crawl` (
  `id` bigint(24) NOT NULL AUTO_INCREMENT COMMENT '标识',
  `list_url` varchar(400) DEFAULT NULL COMMENT '列表地址',
  `list_item_selector` varchar(200) DEFAULT NULL COMMENT '列表中每一个内容的css选择器',
  `proxy_ip` varchar(600) DEFAULT NULL COMMENT '当前采集任务使用的代理ip通过;分割',
  `column_id` bigint(24) DEFAULT NULL COMMENT '放在哪个栏目之下',
  `gmt_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录生成时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '记录修改时间',
  `creator` varchar(32) NOT NULL DEFAULT 'system' COMMENT '创建者',
  `modifier` varchar(32) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `is_deleted` char(1) NOT NULL DEFAULT 'N' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='采集配置']zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;
SET FOREIGN_KEY_CHECKS = 0]zdw;
drop table if exists lzz_crawl_detail]zdw;
CREATE TABLE `lzz_crawl_detail` (
  `id` bigint(24) NOT NULL AUTO_INCREMENT COMMENT '标识',
  `crawl_id` bigint(24) NOT NULL COMMENT 'lzz_crawl外键',
  `field_name` varchar(400) DEFAULT NULL COMMENT '字段名：comm_title,mainbody',
  `field_selector` varchar(200) DEFAULT NULL COMMENT '字段的css选择器',
  `gmt_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录生成时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '记录修改时间',
  `creator` varchar(32) NOT NULL DEFAULT 'system' COMMENT '创建者',
  `modifier` varchar(32) NOT NULL DEFAULT 'system' COMMENT '修改者',
  `is_deleted` char(1) NOT NULL DEFAULT 'N' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='采集明细配置']zdw;
SET FOREIGN_KEY_CHECKS = 1]zdw;