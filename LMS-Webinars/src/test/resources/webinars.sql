INSERT INTO webinars (id, name, label, theme, group_name, date, link, record)
VALUES (default, '1_First', '1. First', 'first', 'Group_1', '2022-10-29', 'https://conference1.link', 'https://record1.link');

INSERT INTO webinars (id, name, label, theme, group_name, date, link, record)
VALUES (default, '2_Second', '2. Second', 'second', 'Group_1', '2022-11-15', 'https://conference1.link',
        'https://record2.link');

INSERT INTO webinars (id, name, label, theme, group_name, date, link, record)
VALUES (default, '1_First', '1. First', 'first', 'Group_2', '2022-11-12', 'https://conference2.link', 'https://record3.link');

INSERT INTO webinars (id, name, label, theme, group_name, date, link, record)
VALUES (default, '2_Second', '2. Second', 'second', 'Group_2', '2022-12-02', 'https://conference2.link',
        'https://record4.link');

INSERT INTO webinars (id, name, label, theme, group_name, date, link, record)
VALUES (default, '5_Fifth', '5. Fifth', 'fifth', 'Group_4', '2023-01-01', 'https://conference4.link',
        'https://record42.link');


INSERT INTO files (id, webinar_id, url, description)
VALUES (default, 1, 'https://files.org/file1.ext', 'Webinar 1 file');

INSERT INTO files (id, webinar_id, url, description)
VALUES (default, 1, 'https://files.org/file2.ext', 'Webinar 1 file');

INSERT INTO files (id, webinar_id, url, description)
VALUES (default, 2, 'https://files.org/file3.ext', 'Webinar 2 file');

INSERT INTO files (id, webinar_id, url, description)
VALUES (default, 2, 'https://files.org/file4.ext', 'Webinar 2 file');

INSERT INTO files (id, webinar_id, url, description)
VALUES (default, 2, 'https://files.org/file5.ext', 'Webinar 2 file');

INSERT INTO files (id, webinar_id, url, description)
VALUES (default, 3, 'https://files.org/file6.ext', 'Webinar 3 file');

INSERT INTO files (id, webinar_id, url, description)
VALUES (default, 5, 'https://files.org/file10.ext', null);

INSERT INTO files (id, webinar_id, url, description)
VALUES (default, 5, 'https://files.org/file10.ext', 'Webinar 1 file');
