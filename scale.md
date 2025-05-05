To scale from 100 to 10 million users/day:

Database: Move from single MySQL instance to a clustered or cloud-managed DB with read replicas and query tuning.

Caching: Externalize Guava cache to a distributed cache like Redis for box office and title validation.

Authentication: Create a separate service for all authentication and authorization responsibilities

Load balancing: Deploy behind a load balancer (e.g., NGINX or Kubernetes Ingress).

Horizontal scaling: Deploy the Spring Boot application as a Kubernetes Deployment with autoscaling enabled

Metrics and Monitoring: Add observability using Prometheus and Grafana.

Testing: Include load tests and chaos engineering to validate high traffic behavior.

