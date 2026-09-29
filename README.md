# ecommerce-springboot

Order Created
│
▼
Kafka order-event
│
▼
Inventory
│
├── decrease stock
│
└── RESERVED
│
┌─────┴─────┐
│           │
Payment OK    Payment Failed
│           │
▼           ▼
COMMITTED   ROLLED_BACK
│
▼
restore stock



Razorpay webhook
│
▼
Payment Service
│
▼
Inventory /rollback
│
│ Kafka event hasn't arrived yet
▼
PENDING ROLLBACK
│
▼
Kafka event arrives later
│
▼
Inventory does NOT decrement stock


                         ┌─────────────────┐
                         │   API Gateway    │
                         │     :8080       │
                         └────────┬────────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │  Order Service  │
                         │     :8083       │
                         └───────┬─────────┘
                                 │
                 ┌───────────────┼────────────────┐
                 │               │                │
                 ▼               ▼                ▼
             Order DB       Inventory check    Kafka
                              Feign             order-event
                                                  │
                                                  ▼
                                           ┌──────────────┐
                                           │  Inventory   │
                                           │    :8081     │
                                           └──────┬───────┘
                                                  │
                                            RESERVED
                                                  │
                         ┌────────────────────────┴───────────────┐
                         │                                        │
                         ▼                                        ▼
                  Payment SUCCESS                         Payment FAILED
                         │                                        │
                         ▼                                        ▼
              /order/{id}/confirm                    /order/{id}/fail
                         │                                        │
                         ▼                                        ▼
                  CONFIRMED                              ROLLED_BACK
                         │                                        │
                         ▼                                        ▼
               /inventory/{id}/commit              /inventory/{id}/rollback
                         │                                        │
                         ▼                                        ▼
                   COMMITTED                              Stock restored