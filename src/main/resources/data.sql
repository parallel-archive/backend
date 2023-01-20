-- Create demo users
INSERT INTO user_detail (name) VALUES ('demo user'); -- 1
INSERT INTO user_detail (name) VALUES ('test user'); -- 2
INSERT INTO user (email, password, user_detail_id) VALUES ('demo@demo.hu', '$2a$10$hxi1u4QScJDBT09hPvarqO9xYyxwb.04kHb.qArN6oCAop.gQT8ZG', 1); -- 1
INSERT INTO user (email, password, user_detail_id) VALUES ('test@test.hu', '$2a$10$hxi1u4QScJDBT09hPvarqO9xYyxwb.04kHb.qArN6oCAop.gQT8ZG' , 2); -- 2


-- Populate Document Filter Types
INSERT INTO document_filter_type (name) VALUES ('TYPE'); -- 1
INSERT INTO document_filter_type (name) VALUES ('LANGUAGE'); -- 2
INSERT INTO document_filter_type (name) VALUES ('COUNTRY'); -- 3


-- Populate TYPE Document Filters
INSERT INTO document_filter (name,document_filter_type_id) VALUES ('TEXT', 1); -- 1
INSERT INTO document_filter (name,document_filter_type_id) VALUES ('IMAGE', 1); -- 2


-- Populate LANGUAGE Document Filters
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('HUNGARIAN', 2); -- 3
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('ENGLISH', 2); -- 4
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('ROMANIAN', 2); -- 5
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('RUSSIAN', 2); -- 6
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('POLISH', 2); -- 7
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('CZECH', 2); -- 8
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('GERMAN', 2); -- 9
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('FRENCH', 2); -- 10
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('BULGARIAN', 2); -- 11
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('PORTUGUESE_BRAZIL', 2); -- 12
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('ALBANIAN', 2); -- 13
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('SERBIAN', 2); -- 14
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('SERBO_CROATIAN', 2); -- 15
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('SLOVAK', 2); -- 16


-- Populate COUNTRY Document Filters
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('RUSSIAN_FEDERATION', 3); -- 17
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('ROMANIA', 3); -- 18
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('HUNGARY', 3); -- 19
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('BELGIUM', 3); -- 20
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('POLAND', 3); -- 21
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('UNITED_STATES', 3); -- 22
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('CZECH_REPUBLIC', 3); -- 23
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('GERMANY', 3); -- 24
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('SLOVAKIA_SLOVAK_REPUBLIC', 3); -- 25
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('AUSTRIA', 3); -- 26
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('TAJIKISTAN', 3); -- 27
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('BOSNIA_AND_HERZEGOVINA', 3); -- 28
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('BULGARIA', 3); -- 29
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('SERBIA', 3); -- 30
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('UNITED_KINGDOM', 3); -- 31
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('AFGHANISTAN', 3); -- 32
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('FRANCE', 3); -- 33
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('ALBANIA', 3); -- 34
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('CROATIA', 3); -- 35
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('LAO_PEOPLES_DEMOCRATIC_REPUBLIC', 3); -- 36
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('CAMBODIA', 3); -- 37
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('VIETNAM', 3); -- 38
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('ITALY', 3); -- 39
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('KAZAKHSTAN', 3); -- 40
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('TURKMENISTAN', 3); -- 41
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('UZBEKISTAN', 3); -- 42
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('AZERBAIJAN', 3); -- 43
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('GREECE', 3); -- 44
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('KYRGYZSTAN', 3); -- 45
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('MACEDONIA_THE_FORMER_YUGOSLAV_REPUBLIC_OF', 3); -- 46
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('BRAZIL', 3); -- 47
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('CUBA', 3); -- 48
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('EGYPT', 3); -- 49
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('SLOVENIA', 3); -- 50
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('SWITZERLAND', 3); -- 51
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('TURKEY', 3); -- 52
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('ARMENIA', 3); -- 53
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('ESTONIA', 3); -- 54
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('ISRAEL', 3); -- 55
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('LATVIA', 3); -- 56
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('LITHUANIA', 3); -- 57
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('SOUTH_AFRICA', 3); -- 58
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('THAILAND', 3); -- 59
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('AMERICAN_SAMOA', 3); -- 60
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('BELARUS', 3); -- 61
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('BOLIVIA', 3); -- 62
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('CANADA', 3); -- 63
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('CHAD', 3); -- 64
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('COLOMBIA', 3); -- 65
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('DOMINICAN_REPUBLIC', 3); -- 66
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('ETHIOPIA', 3); -- 67
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('FINLAND', 3); -- 68
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('GEORGIA', 3); -- 69
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('GUATEMALA', 3); -- 70
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('INDONESIA', 3); -- 71
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('IRAN_ISLAMIC_REPUBLIC_OF', 3); -- 72
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('IRAQ', 3); -- 73
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('JAPAN', 3); -- 74
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('LIBYAN_ARAB_JAMAHIRIYA', 3); -- 75
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('LUXEMBOURG', 3); -- 76
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('MOLDOVA_REPUBLIC_OF', 3); -- 77
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('NETHERLANDS', 3); -- 78
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('NORWAY', 3); -- 79
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('PANAMA', 3); -- 80
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('PERU', 3); -- 81
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('PUERTO_RICO', 3); -- 82
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('SWEDEN', 3); -- 83
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('UKRAINE', 3); -- 84
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('VENEZUELA', 3); -- 85
INSERT INTO document_filter (name, document_filter_type_id) VALUES ('ZIMBABWE', 3); -- 86
                                                                                      

