# bigtable-schema-edd-utility

  This is a sprint boot utility app which manages the Bigtable schema(Create/Update/Delete tables). It reads input from GCS in YAML format (As shown below) and takes the necessary. 
  actions. 
  
 YAML Format :   
# ALLOWED VALUES FOR ACTION ARE
  # CREATE_TABLE
  # DELETE_TABLE
  # ADD_COLUMN_FAMILY
  # DELETE_COLUMN_FAMILY
---
tables:
  - table:
      action: CREATE_TABLE
      name: product-demo
      column-families:
        - name: product-time-details
  - table:
      name: category-demo
      action: CREATE_TABLE
      column-families:
        - name: category-time-details


Used a JIB plug in to generate and push the image to GCR.

JAVA : 17
Gradle Commands :
    Gradle clean build jib 

Set GOOGLE_APPLICATION_CREDENTIALS  env variable. 

Roles Required in GCP :
  . BigTable Admin
  . GCS Reader 
  . GCR Pull and Push
  
We can use this image to run in CloudRun. 

API Relative Path : /schema-update/bigtable-edd-18-Aug-2022.yml

    
