# Phase 9 — Controlled agent decisions

These decisions extend the existing assistant, RAG, and conversation services. They do not add a second orchestration framework.

## Agent runtime

The agent is a bounded loop in `app/orchestration/agent`. The language model returns JSON that names a tool or a final answer. The runtime validates that JSON and refuses to execute anything else. LangGraph remains an older extension point and is not used here, because a registry, a policy check, and a step limit are enough.

## Tool registry

Tools are Python objects registered in code. The model cannot import modules, name a function, or add a tool at runtime. Names such as `python`, `shell`, `sql`, and `http` cannot be registered.

## Tool policy

Every proposal is checked for owner or tenant mismatch, registry membership, caller permission, input schema, and risk. A denial stops the run before the tool is called. Retrieved text and tool output are passed back as untrusted data.

## Human approval

`HIGH` and `CRITICAL` proposals, and any tool that is not read-only, stop in `WAITING_FOR_APPROVAL`. The owner may approve or reject. Approval is audited and does not execute the tool in this phase, because high-risk tools are not enabled.

## Execution limits

The loop stops on a maximum number of steps, tool calls, tokens, estimated cost, or wall-clock time. Repeating the same tool with the same arguments stops the run. Read-only failures may be retried once. Timeouts are not retried. Cancellation is checked before the next tool call.

## Read-only first

The production catalog is `search_knowledge`, `retrieve_knowledge`, `search_conversations`, and `search_content`. Knowledge search calls the Phase 5 retriever. Conversation search reads only the caller's Phase 6 rows. Nothing in this catalog creates, updates, deletes, or publishes ACOS content.
