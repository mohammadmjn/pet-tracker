--- Cats
INSERT INTO PET (pet_type, owner_id, in_zone)
VALUES ('CAT', 1, true);
INSERT INTO CAT (id, tracker_type, lost_tracker)
VALUES ((SELECT MAX(id) FROM PET),'SMALL', false);

INSERT INTO PET (pet_type, owner_id, in_zone)
VALUES ('CAT', 11, false);
INSERT INTO CAT (id, tracker_type, lost_tracker)
VALUES ((SELECT MAX(id) FROM PET),'SMALL', false);

INSERT INTO PET (pet_type, owner_id, in_zone)
VALUES ('CAT', 111, true);
INSERT INTO CAT (id, tracker_type, lost_tracker)
VALUES ((SELECT MAX(id) FROM PET),'BIG', false);

INSERT INTO PET (pet_type, owner_id, in_zone)
VALUES ('CAT', 1111, false);
INSERT INTO CAT (id, tracker_type, lost_tracker)
VALUES ((SELECT MAX(id) FROM PET),'BIG', true);

--- Dogs
INSERT INTO PET (pet_type, owner_id, in_zone)
VALUES ('DOG', 2, true);
INSERT INTO DOG (id, tracker_type)
VALUES ((SELECT MAX(id) FROM PET),'BIG');

INSERT INTO PET (pet_type, owner_id, in_zone)
VALUES ('DOG', 22, false);
INSERT INTO DOG (id, tracker_type)
VALUES ((SELECT MAX(id) FROM PET),'BIG');

INSERT INTO PET (pet_type, owner_id, in_zone)
VALUES ('DOG', 222, true);
INSERT INTO DOG (id, tracker_type)
VALUES ((SELECT MAX(id) FROM PET),'MEDIUM');

INSERT INTO PET (pet_type, owner_id, in_zone)
VALUES ('DOG', 2222, false);
INSERT INTO DOG (id, tracker_type)
VALUES ((SELECT MAX(id) FROM PET),'MEDIUM');

INSERT INTO PET (pet_type, owner_id, in_zone)
VALUES ('DOG', 5, true);
INSERT INTO DOG (id, tracker_type)
VALUES ((SELECT MAX(id) FROM PET),'SMALL');

INSERT INTO PET (pet_type, owner_id, in_zone)
VALUES ('DOG', 6, false);
INSERT INTO DOG (id, tracker_type)
VALUES ((SELECT MAX(id) FROM PET),'SMALL');