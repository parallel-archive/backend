drop table if exists document_tag_published_document_meta_data;
drop table if exists document_tag_document_meta_data;
drop table if exists document_tag;
drop table if exists published_document_meta_data_document_filter;
drop table if exists document_meta_data_document_filter;
drop table if exists document_filter;
drop table if exists document_filter_type;
drop table if exists ocr_rate_limit;
drop table if exists document_image;
drop table if exists document;
drop table if exists document_meta_data;
drop table if exists collection_published_document;
drop table if exists published_document_annotation;
drop table if exists published_document_ocr;
drop table if exists published_document;
drop table if exists published_document_meta_data;
drop table if exists collection;
drop table if exists image;
drop table if exists password_reset_token;
drop table if exists registration_token;
drop table if exists user;
drop table if exists my_shoe_box;
drop table if exists user_detail;

create table if not exists user_detail (id bigint not null auto_increment, name varchar(255) not null, primary key (id));
create table if not exists my_shoe_box (id bigint not null auto_increment, name varchar(255) not null, primary key (id));
create table if not exists user (id bigint not null auto_increment, email varchar(255) not null, password varchar(255) not null, enabled bool default false, user_detail_id bigint, my_shoe_box_id bigint, display_name varchar(255) not null, public_email bool default true, primary key (id), constraint FK_USER_DETAIL_ID foreign key (user_detail_id) references user_detail (id), constraint FK_MY_SHOE_BOX_ID foreign key (my_shoe_box_id) references my_shoe_box (id));

create table if not exists registration_token (id bigint not null auto_increment, token varchar(255), user_id bigint, primary key (id), constraint FK_REGISTRATION_TOKEN_USER_ID foreign key (user_id) references user (id));
create table if not exists password_reset_token (id bigint not null auto_increment, token varchar(255), user_id bigint, primary key (id), constraint FK_PASSWORD_RESET_TOKEN_USER_ID foreign key (user_id) references user (id));

create table if not exists image (id bigint not null auto_increment, name varchar(255) not null, url varchar(255) not null, active_url varchar(255), thumbnail_url varchar(255), active_thumbnail_url varchar(255), rotation bigint not null , uploaded_at timestamp not null, modified_at timestamp not null, my_shoe_box_id bigint not null, primary key (id), constraint FK_IMAGE_MY_SHOE_BOX_ID foreign key (my_shoe_box_id) references my_shoe_box (id));

create table if not exists published_document_meta_data (id bigint not null auto_increment, original_title varchar(255), original_author varchar(255), created_at_year bigint, period_covered_from bigint, period_covered_to bigint, archive_name varchar(255), archive_category varchar(255), publication varchar(255), catalog_url varchar(255), source_url varchar(255), reference_code varchar(255), primary key (id));
create table if not exists published_document (id bigint not null auto_increment, hash varchar(255), created_at timestamp not null, pdf_url varchar(255), thumbnail_url varchar(255), published_document_meta_data_id bigint not null, user_id bigint not null, views bigint not null, ipfs_content_id varchar(255), status varchar(255) not null, primary key (id), constraint FK_PUBLISHED_DOCUMENT_META_DATA_ID foreign key (published_document_meta_data_id) references published_document_meta_data (id), constraint FK_PUBLISHED_DOCUMENT_USER_ID foreign key (user_id) references user (id));
create table if not exists published_document_ocr (id bigint not null auto_increment, published_document_id bigint not null, page_index bigint not null, original_ocr text(65535), edited_ocr text(65535), primary key (id), constraint FK_PUBLISHED_DOCUMENT_ID foreign key (published_document_id) references published_document (id)); 
create table if not exists published_document_annotation (id bigint not null auto_increment, published_document_id bigint not null, user_id bigint not null, annotation varchar(255), primary key (id), constraint FK_ANNOTATION_PUBLISHED_DOCUMENT_ID foreign key (published_document_id) references published_document (id), constraint FK_ANNOTATION_USER_ID foreign key (user_id) references user (id));

create table if not exists document_meta_data (id bigint not null auto_increment, original_title varchar(255), original_author varchar(255), created_at_year bigint, period_covered_from bigint, period_covered_to bigint, archive_name varchar(255), archive_category varchar(255), publication varchar(255), catalog_url varchar(255), source_url varchar(255), reference_code varchar(255), primary key (id));
create table if not exists document (id bigint not null auto_increment, uploaded_at timestamp not null, modified_at timestamp not null, meta_data_id bigint not null, user_id bigint not null, published_document_id bigint, primary key (id), constraint FK_DOCUMENT_META_DATA_ID foreign key (meta_data_id) references document_meta_data (id), constraint FK_USER_ID foreign key (user_id) references user (id), constraint FK_DOCUMENT_PUBLISHED_DOCUMENT_ID foreign key (published_document_id) references published_document (id));
create table if not exists document_image (id bigint not null auto_increment, name varchar(255) not null, image_index bigint not null, url varchar(255) not null, thumbnail_url varchar(255), uploaded_at timestamp not null, original_ocr text(65535), edited_ocr text(65535), document_id bigint not null, primary key (id), constraint FK_DOCUMENT_ID foreign key (document_id) references document (id));  

create table if not exists collection (id bigint not null auto_increment, name varchar(255) not null, user_id bigint not null, primary key (id), constraint FK_COLLECTION_USER_ID foreign key (user_id) references user (id));
create table if not exists collection_published_document (collection_id bigint not null, published_document_id bigint not null, constraint FK_COLLECTION_OF_PUBLISHED_DOCUMENTS_ID foreign key (collection_id) references collection (id), constraint FK_COLLECTION_PUBLISHED_DOCUMENTS_ID foreign key (published_document_id) references published_document (id));

create table if not exists ocr_rate_limit (id bigint not null auto_increment, page bigint not null, user_id bigint not null, primary key (id), constraint FK_OCR_RATE_LIMIT_USER_ID foreign key (user_id) references user (id), constraint user_id_unique UNIQUE (user_id));

create table if not exists document_tag(id bigint not null auto_increment, name varchar(255) not null, primary key (id), constraint document_tag_name_unique unique (name));
create table if not exists document_tag_document_meta_data (document_tag_id bigint not null, document_meta_data_id bigint not null, constraint FK_DOCUMENT_TAG_ID foreign key (document_tag_id) references document_tag (id), constraint FK_DOCUMENT_META_DATA_OF_TAG_ID foreign key (document_meta_data_id) references document_meta_data (id));
create table if not exists document_tag_published_document_meta_data (document_tag_id bigint not null, published_document_meta_data_id bigint not null, constraint FK_PUBLISHED_DOCUMENT_TAG_ID foreign key (document_tag_id) references document_tag (id), constraint FK_PUBLISHED_DOCUMENT_META_DATA_OF_TAG_ID foreign key (published_document_meta_data_id) references published_document_meta_data (id));

create table if not exists document_filter_type (id bigint not null auto_increment, name varchar(255) not null, primary key (id));
create table if not exists document_filter (id bigint not null auto_increment, name varchar(255) not null, document_filter_type_id bigint, primary key (id), constraint FK_DOCUMENT_FILTER_DOCUMENT_FILTER_TYPE_ID foreign key (document_filter_type_id) references document_filter_type(id));
create table if not exists document_meta_data_document_filter (document_meta_data_id bigint not null, document_filter_id bigint not null, constraint FK_DOCUMENT_META_DATA_OF_DOCUMENT_FILTERS_ID foreign key (document_meta_data_id) references document_meta_data (id), constraint FK_DOCUMENT_FILTER_ID foreign key (document_filter_id) references document_filter (id));
create table if not exists published_document_meta_data_document_filter (published_document_meta_data_id bigint not null, document_filter_id bigint not null, constraint FK_PUBLISHED_DOCUMENT_META_DATA_OF_DOCUMENT_FILTERS_ID foreign key (published_document_meta_data_id) references published_document_meta_data (id), constraint FK_PUBLISHED_DOCUMENT_FILTER_ID foreign key (document_filter_id) references document_filter (id));
