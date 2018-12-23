resource "aws_iam_role" "cloudtrail_forwarder_role" {
  name = "FEYECloudTrailRole"
  path = "/"

  assume_role_policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [{
    "Effect": "Allow",
    "Principal": {
        "AWS": "arn:aws:iam::264756907367:role/service_infra_sre@lambda_cloudtrail_ingestion_role"
    },
    "Action": "sts:AssumeRole",
    "Condition": {
        "StringEquals": {
            "sts:ExternalId": "${var.external_id}"
        }
    }
    }]
}
EOF
}

resource "aws_iam_policy" "cloudtrail_forwarder_policy" {
  name        = "FEYEAccessToCloudTrail"
  path        = "/"
  description = "FireEye Access to Bucket"


  policy = <<EOF
{
   "Version": "2012-10-17",
   "Statement":[
      {
         "Action":[
            "s3:Get*",
            "s3:List*"
         ],
         "Effect":"Allow",
         "Resource":"arn:aws:s3:::${var.cloudtrail_bucket}",
         "Condition":{
            "StringLike":{
               "s3:prefix":"${var.log_file_prefix}"
            }
         }
      },
      {
         "Action":[
            "s3:Get*",
            "s3:List*"
         ],
         "Effect":"Allow",
         "Resource":"arn:aws:s3:::${var.cloudtrail_bucket}"
      }
   ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "attach-forwarder-policy-to-role" {
  role       = "${aws_iam_role.cloudtrail_forwarder_role.name}"
  policy_arn = "${aws_iam_policy.cloudtrail_forwarder_policy.arn}"
}

resource "aws_s3_bucket_notification" "cloudtrail_bucket_notification" {
  bucket = "${var.cloudtrail_bucket_id}"

  lambda_function {
    lambda_function_arn = "arn:aws:lambda:us-east-1:264756907367:function:LambdaCT"
    events              = ["s3:ObjectCreated:*", "s3:ObjectRemoved:*"]
    filter_prefix       = "AWSLogs/361326022344/" # DE Prod
  }
  
  lambda_function {
    lambda_function_arn = "arn:aws:lambda:us-east-1:264756907367:function:LambdaCT"
    events              = ["s3:ObjectCreated:*", "s3:ObjectRemoved:*"]
    filter_prefix       = "AWSLogs/769738661673/" # DE Non Prod
  }
}
