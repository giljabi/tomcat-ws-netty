CREATE USER gatewayuser WITH password 'gatewayuser';
CREATE DATABASE gateway OWNER gatewaydb_user ENCODING 'UTF8'
GRANT ALL PRIVILEGES ON DATABASE gateway TO gatewayuser;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO gatewayuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO gatewayuser;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO gatewayuser;

NOTIFY serverchannel, 'GATEWAY';

DROP TABLE IF EXISTS public.userinfo;
CREATE TABLE public.userinfo (
                                 user_id varchar(20) NOT NULL,
                                 user_nm varchar(20) NOT NULL,
                                 user_pw varchar(100) NOT NULL,
                                 terminal_id int NULL,
                                 created_at timestamp NOT NULL DEFAULT now(),
                                 created_by varchar(20) NULL,
                                 updated_at timestamp NULL,
                                 updated_by varchar(20) NULL,
                                 CONSTRAINT userinfo_pk PRIMARY KEY (user_id)
);

-- INSERT 사용자정보, qweqwe123
INSERT INTO public.userinfo (user_id, user_nm, user_pw, terminal_id, created_at, created_by, updated_at, updated_by)
VALUES('admin', '최고관리자', '$2a$10$.aIOpxx1hFeQHN.Kw9m0iOt3OaNvS7.JQNJylaRPy/J5nIgnSnCCq', 9999, now(), '', now(), 'admin');

INSERT INTO public.userinfo (user_id, user_nm, user_pw, terminal_id, created_at, created_by, updated_at, updated_by)
VALUES('admin0', '최고관리자', '$2a$10$.aIOpxx1hFeQHN.Kw9m0iOt3OaNvS7.JQNJylaRPy/J5nIgnSnCCq', 1000, now(), '', now(), 'admin');

INSERT INTO public.userinfo (user_id, user_nm, user_pw, terminal_id, created_at, created_by, updated_at, updated_by)
VALUES('admin1', '최고관리자', '$2a$10$.aIOpxx1hFeQHN.Kw9m0iOt3OaNvS7.JQNJylaRPy/J5nIgnSnCCq', 1001, now(), '', now(), 'admin');

INSERT INTO public.userinfo (user_id, user_nm, user_pw, terminal_id, created_at, created_by, updated_at, updated_by)
VALUES('admin2', '최고관리자', '$2a$10$.aIOpxx1hFeQHN.Kw9m0iOt3OaNvS7.JQNJylaRPy/J5nIgnSnCCq', 1002, now(), '', now(), 'admin');

INSERT INTO public.userinfo (user_id, user_nm, user_pw, terminal_id, created_at, created_by, updated_at, updated_by)
VALUES('admin3', '최고관리자', '$2a$10$.aIOpxx1hFeQHN.Kw9m0iOt3OaNvS7.JQNJylaRPy/J5nIgnSnCCq', 1003, now(), '', now(), 'admin');

INSERT INTO public.userinfo (user_id, user_nm, user_pw, terminal_id, created_at, created_by, updated_at, updated_by)
VALUES('admin4', '최고관리자', '$2a$10$.aIOpxx1hFeQHN.Kw9m0iOt3OaNvS7.JQNJylaRPy/J5nIgnSnCCq', 1004, now(), '', now(), 'admin');

INSERT INTO public.userinfo (user_id, user_nm, user_pw, terminal_id, created_at, created_by, updated_at, updated_by)
VALUES('admin5', '최고관리자', '$2a$10$.aIOpxx1hFeQHN.Kw9m0iOt3OaNvS7.JQNJylaRPy/J5nIgnSnCCq', 1005, now(), '', now(), 'admin');

INSERT INTO public.userinfo (user_id, user_nm, user_pw, terminal_id, created_at, created_by, updated_at, updated_by)
VALUES('admin6', '최고관리자', '$2a$10$.aIOpxx1hFeQHN.Kw9m0iOt3OaNvS7.JQNJylaRPy/J5nIgnSnCCq', 1006, now(), '', now(), 'admin');

INSERT INTO public.userinfo (user_id, user_nm, user_pw, terminal_id, created_at, created_by, updated_at, updated_by)
VALUES('admin7', '최고관리자', '$2a$10$.aIOpxx1hFeQHN.Kw9m0iOt3OaNvS7.JQNJylaRPy/J5nIgnSnCCq', 1007, now(), '', now(), 'admin');

INSERT INTO public.userinfo (user_id, user_nm, user_pw, terminal_id, created_at, created_by, updated_at, updated_by)
VALUES('admin8', '최고관리자', '$2a$10$.aIOpxx1hFeQHN.Kw9m0iOt3OaNvS7.JQNJylaRPy/J5nIgnSnCCq', 1008, now(), '', now(), 'admin');

INSERT INTO public.userinfo (user_id, user_nm, user_pw, terminal_id, created_at, created_by, updated_at, updated_by)
VALUES('admin9', '최고관리자', '$2a$10$.aIOpxx1hFeQHN.Kw9m0iOt3OaNvS7.JQNJylaRPy/J5nIgnSnCCq', 1009, now(), '', now(), 'admin');


--응답받은 클라이언트 상태정보
DROP TABLE IF EXISTS public.boothstatus;
CREATE TABLE public.boothstatus (
                                    servertime timestamp NULL DEFAULT now(), -- 서버시간
                                    terminalid varchar(32) not null,
                                    status varchar(4) not null default '0000',
                                    clientsendtime timestamp null,
                                    recvmessage varchar(1024),
                                    lastcommand varchar(16) not null,
                                    localIp varchar(64),
                                    publicIp varchar(64),
                                    constraint boothstatus_pk primary key(terminalid)
);

-- netty channel 정보
DROP TABLE IF EXISTS public.nettychannel;
CREATE TABLE public.nettychannel (
                                     terminalid varchar(32) not null,
                                     channelId varchar(32),
                                     channel varchar(64),
                                     datetime timestamp default now(),
                                     constraint nettychannel_pk primary key(terminalid)
);

--클라이언트 디렉토리 목록,8192 미만데이터 길이를 초과하면 예외처리됨
DROP TABLE IF EXISTS public.mq_server;
CREATE TABLE public.mq_server (
                                  datetime timestamp NULL DEFAULT now(),
                                  sendserver varchar(10) NOT NULL, -- api, admin, gateway insert/update 서버
                                  recvserver varchar(10) not null, -- api, admin, gateway 전송받을 서버
                                  message varchar(1024) null, --json으로 전달
                                  constraint mq_recvserver_pk primary key(recvserver)
);

INSERT INTO public.mq_server (message, recvserver, sendserver)
VALUES ('{"type":"connect","data":"OK"}', 'GATEWAY', 'ADMIN')
ON CONFLICT (recvserver) DO
    UPDATE SET
               datetime = now(),
               message = EXCLUDED.message,
               sendserver = EXCLUDED.sendserver;

-- create notify function
CREATE OR REPLACE FUNCTION public.fn_mq_server_notify_trigger()
    RETURNS trigger
    LANGUAGE plpgsql
AS $$
BEGIN
    PERFORM pg_notify('serverchannel', jsonb_build_object(
            'datetime', NEW.datetime,
            'sendserver', NEW.sendserver,
            'recvserver', NEW.recvserver
        )::text);
    RETURN NEW;
END;
$$;

--Postgresql 11 이상은 EXECUTE FUNCTION을 사용해야 함
DROP TRIGGER IF EXISTS mq_server_notify_trigger ON mq_server;
CREATE TRIGGER mq_server_notify_trigger
    AFTER INSERT OR UPDATE ON mq_server
    FOR EACH ROW EXECUTE FUNCTION fn_mq_server_notify_trigger();

