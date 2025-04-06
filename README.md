# Agrona Primer

[![Build](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/your-username/agrona-primer)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A Java playground to explore [Agrona](https://github.com/real-logic/agrona), an efficient data structures and utilities library designed for low-latency systems. This repo demonstrates core components of Agrona in action, including custom agents, off-heap buffers, counters, ring buffers, error logging, and thread-safe buffer sharing.

---

## 🚀 Features

- 📦 **UnsafeBuffer** usage (on-heap/off-heap)
- 🧮 **Atomic counters** and **CountersManager**
- 🔁 **Ring buffers** (`OneToOneRingBuffer`)
- 🧰 **DirectBufferInputStream/OutputStream**
- ⚠️ **DistinctErrorLog** integration with stack trace control
- 👷‍♂️ **AgentRunner** with idle strategy for background task scheduling
- 🧵 **Thread-safe buffer sharing** examples
- 🧠 **Low-GC design using Agrona data structures**

---

## 📂 Project Structure

```bash
.
├── config/                          # Custom buffer sizing utilities
├── threadsafe/                     # Multi-threaded buffer communication examples
│   ├── ThreadSafeOffHeapMain.java
│   ├── ThreadSafeReaderAgent.java
│   └── ThreadSafeWriterAgent.java
├── agent/                          # Background agent demos using AgentRunner
│   ├── ErrorMonitoringAgent.java   # Tracks error frequency and logs with controls
│   ├── ErrorAgentRunnerMain.java   # Main class to simulate errors & start agent
│   └── HeartbeatAgent.java
├── AgronaAllFeaturesDemo.java      # Kitchen-sink demo of most Agrona APIs
├── BufferProducer/Consumer.java    # Sample off-heap direct buffer transfer
└── BufferConfig.java               # Dynamic buffer config calculator
```

---

## 📦 Dependencies

- **Agrona**: `org.agrona:agrona:1.18.0`
- **SLF4J + Logback** for logging

Maven coordinates already configured in the main `pom.xml`.

---

## 🏁 How to Run

1. Clone the repo:
   ```bash
   git clone https://github.com/your-username/agrona-primer.git
   cd agrona-primer
   ```

2. Build with Maven:
   ```bash
   mvn clean install
   ```

3. Run the default main class (as defined in `pom.xml`):
   ```bash
   mvn exec:java
   ```

### ✨ Run specific demos manually:

You can run specific classes from your IDE or CLI using:

```bash
# Example
java -cp target/agrona-primer-1.0-SNAPSHOT.jar com.sanjeevas.agrona.AgronaAllFeaturesDemo
```

---

## 🧪 Interesting Classes

### 🔹 `AgronaAllFeaturesDemo.java`

- A single-file demo for:
    - Lists, Maps, Sets
    - RingBuffer write/read
    - Off-heap streaming
    - Counter management
    - Signal handling (`ShutdownSignalBarrier`)

---

### 🔹 `ErrorMonitoringAgent.java`

- Logs and tracks recurring `Throwable` messages
- Only prints **stack trace once per error**
- Keeps frequency of recurring issues
- Integrates with `DistinctErrorLog`
- Designed for `AgentRunner`-based runtime

---

### 🔹 `ThreadSafeOffHeapMain.java`

- Demonstrates off-heap buffer sharing using `UnsafeBuffer`
- One writer thread, one reader thread, both working on the same direct memory block

---

## 💡 Why Agrona?

Agrona is a performance-first library built for high-throughput, low-latency systems. It is commonly used in:
- Messaging systems (like [Aeron](https://github.com/real-logic/aeron))
- Low-GC microservices
- Financial/quant trading systems

---

## 📌 Key Design Takeaways

- GC-less tracking using `Object2IntHashMap` / `ObjectHashSet`
- Buffer alignment & size correctness using `BufferConfig`
- Decoupled error monitoring with **Agent-based design**
- Graceful shutdown with `ShutdownSignalBarrier`

---

## ⚠️ Notes

- Not all classes are thread-safe. Some rely on single-threaded access via `AgentRunner`.
- Make sure to **tune buffer sizes appropriately** in `BufferConfig` for production-grade usage.

---

## 📜 License

This project is licensed under the [MIT License](LICENSE).

---

## 🙋‍♂️ Author

Made with ❤️ by [@sanjeevas](https://github.com/srinathSanjeeva)
