resource "aws_route53_record" "api-k8s" {
  name = "api.${var.cluster_name}"
  type = "A"

  alias = {
    name                   = "${aws_elb.api.dns_name}"
    zone_id                = "${aws_elb.api.zone_id}"
    evaluate_target_health = false
  }

  zone_id = "/hostedzone/${var.route53_zoneid}"
}
