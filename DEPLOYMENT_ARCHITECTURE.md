# Enterprise Microservices Deployment Architecture

## Infrastructure Overview

### Cloud Provider: AWS
- **Region**: us-east-1 (Primary), us-west-2 (DR)
- **Availability Zones**: 3 AZs for high availability
- **VPC**: Custom VPC with public/private subnets

### Container Orchestration: Kubernetes (EKS)
- **Cluster**: Managed EKS cluster
- **Node Groups**: Multiple node groups for different workloads
- **Auto Scaling**: Horizontal Pod Autoscaler (HPA)

## Network Architecture

### VPC Configuration
```
VPC: 10.0.0.0/16
├── Public Subnets (10.0.1.0/24, 10.0.2.0/24, 10.0.3.0/24)
│   ├── Load Balancer
│   ├── NAT Gateway
│   └── Bastion Host
└── Private Subnets (10.0.10.0/24, 10.0.20.0/24, 10.0.30.0/24)
    ├── EKS Cluster
    ├── RDS Database
    └── ElastiCache Redis
```

### Security Groups
```
ALB Security Group:
- Inbound: 80, 443 from 0.0.0.0/0
- Outbound: 8080-8084 to EKS Security Group

EKS Security Group:
- Inbound: 8080-8084 from ALB Security Group
- Outbound: 5432 to RDS Security Group
- Outbound: 6379 to ElastiCache Security Group

RDS Security Group:
- Inbound: 5432 from EKS Security Group
- Outbound: All traffic

ElastiCache Security Group:
- Inbound: 6379 from EKS Security Group
- Outbound: All traffic
```

## Service Deployment

### 1. Load Balancer Layer
```yaml
# AWS Application Load Balancer
- Type: Application Load Balancer
- Scheme: Internet-facing
- Subnets: Public subnets across 3 AZs
- Security Groups: ALB-SG
- Target Groups: EKS cluster nodes
- Health Checks: HTTP health check on /health
- SSL Certificate: AWS Certificate Manager
```

### 2. EKS Cluster Configuration
```yaml
# EKS Cluster
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig

metadata:
  name: microservices-cluster
  region: us-east-1
  version: "1.28"

nodeGroups:
  - name: general-workers
    instanceType: t3.medium
    desiredCapacity: 3
    minSize: 1
    maxSize: 10
    volumeSize: 20
    ssh:
      allow: true
      publicKeyName: microservices-key

  - name: database-workers
    instanceType: t3.large
    desiredCapacity: 2
    minSize: 1
    maxSize: 5
    volumeSize: 50
    labels:
      node-type: database
```

### 3. Service Deployments

#### API Gateway Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
spec:
  replicas: 3
  selector:
    matchLabels:
      app: api-gateway
  template:
    metadata:
      labels:
        app: api-gateway
    spec:
      containers:
      - name: api-gateway
        image: microservices/api-gateway:latest
        ports:
        - containerPort: 8080
        env:
        - name: EUREKA_SERVER
          value: "http://discovery-service:8761/eureka/"
        - name: CONFIG_SERVER
          value: "http://config-server:8888"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
```

#### Employee Service Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: employee-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: employee-service
  template:
    metadata:
      labels:
        app: employee-service
    spec:
      containers:
      - name: employee-service
        image: microservices/employee-service:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod,redis,s3"
        - name: DB_HOST
          value: "postgresql-cluster.cluster-xyz.us-east-1.rds.amazonaws.com"
        - name: REDIS_HOST
          value: "microservices-redis.abc123.cache.amazonaws.com"
        - name: AWS_S3_BUCKET
          value: "microservices-employee-files"
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 10
```

### 4. Database Configuration

#### RDS PostgreSQL Cluster
```yaml
# RDS Configuration
DBInstanceClass: db.r5.large
Engine: postgres
EngineVersion: "14.9"
MultiAZ: true
AllocatedStorage: 100
StorageType: gp3
StorageEncrypted: true
BackupRetentionPeriod: 7
PreferredBackupWindow: "03:00-04:00"
PreferredMaintenanceWindow: "sun:04:00-sun:05:00"
```

#### ElastiCache Redis Cluster
```yaml
# ElastiCache Configuration
CacheNodeType: cache.r6g.large
Engine: redis
EngineVersion: "7.0"
NumCacheNodes: 3
CacheSubnetGroupName: microservices-cache-subnet-group
SecurityGroupIds: [redis-sg]
AtRestEncryptionEnabled: true
TransitEncryptionEnabled: true
```

### 5. Monitoring Stack

#### Prometheus Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: prometheus
spec:
  replicas: 1
  selector:
    matchLabels:
      app: prometheus
  template:
    metadata:
      labels:
        app: prometheus
    spec:
      containers:
      - name: prometheus
        image: prom/prometheus:latest
        ports:
        - containerPort: 9090
        volumeMounts:
        - name: prometheus-config
          mountPath: /etc/prometheus
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
      volumes:
      - name: prometheus-config
        configMap:
          name: prometheus-config
```

#### Grafana Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: grafana
spec:
  replicas: 1
  selector:
    matchLabels:
      app: grafana
  template:
    metadata:
      labels:
        app: grafana
    spec:
      containers:
      - name: grafana
        image: grafana/grafana:latest
        ports:
        - containerPort: 3000
        env:
        - name: GF_SECURITY_ADMIN_PASSWORD
          valueFrom:
            secretKeyRef:
              name: grafana-secret
              key: admin-password
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
```

## Auto Scaling Configuration

### Horizontal Pod Autoscaler
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: employee-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: employee-service
  minReplicas: 3
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

### Cluster Autoscaler
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: cluster-autoscaler
spec:
  replicas: 1
  selector:
    matchLabels:
      app: cluster-autoscaler
  template:
    metadata:
      labels:
        app: cluster-autoscaler
    spec:
      containers:
      - name: cluster-autoscaler
        image: k8s.gcr.io/autoscaling/cluster-autoscaler:v1.28.0
        command:
        - ./cluster-autoscaler
        - --v=4
        - --stderrthreshold=info
        - --cloud-provider=aws
        - --skip-nodes-with-local-storage=false
        - --expander=least-waste
        - --node-group-auto-discovery=asg:tag=k8s.io/cluster-autoscaler/enabled,k8s.io/cluster-autoscaler/microservices-cluster
        resources:
          limits:
            cpu: 100m
            memory: 300Mi
          requests:
            cpu: 100m
            memory: 300Mi
```

## Security Configuration

### Network Policies
```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: microservices-network-policy
spec:
  podSelector:
    matchLabels:
      app: microservices
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: api-gateway
    ports:
    - protocol: TCP
      port: 8080-8084
  egress:
  - to:
    - podSelector:
        matchLabels:
          app: postgresql
    ports:
    - protocol: TCP
      port: 5432
  - to:
    - podSelector:
        matchLabels:
          app: redis
    ports:
    - protocol: TCP
      port: 6379
```

### RBAC Configuration
```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: microservices-role
rules:
- apiGroups: [""]
  resources: ["pods", "services", "endpoints"]
  verbs: ["get", "list", "watch"]
- apiGroups: ["apps"]
  resources: ["deployments", "replicasets"]
  verbs: ["get", "list", "watch"]
```

## Backup and Disaster Recovery

### Database Backup
```bash
# Automated RDS snapshots
aws rds create-db-snapshot \
  --db-instance-identifier microservices-cluster \
  --db-snapshot-identifier microservices-backup-$(date +%Y%m%d)
```

### Configuration Backup
```bash
# Backup Kubernetes configurations
kubectl get all -o yaml > microservices-backup-$(date +%Y%m%d).yaml
```

### Disaster Recovery Plan
1. **RTO**: 5 minutes
2. **RPO**: 1 minute
3. **Multi-region deployment**
4. **Automated failover**
5. **Data replication**

## Cost Optimization

### Resource Right-sizing
- **Development**: t3.medium instances
- **Production**: t3.large instances
- **Database**: r5.large instances
- **Cache**: cache.r6g.large instances

### Cost Monitoring
- **AWS Cost Explorer**
- **Resource tagging**
- **Budget alerts**
- **Regular cost reviews**

This deployment architecture provides a robust, scalable, and secure foundation for the enterprise microservices platform.
