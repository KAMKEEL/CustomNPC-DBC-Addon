# Expert-Scripter Agent Guidelines

This document defines the conventions and quality standards for **Expert-Scripter-subagent** when generating CustomNPC+ and DBC Addon scripts for Minecraft 1.7.10.

---

## ⚠️ CRITICAL: GIT SAFETY PROTOCOL

**NO AGENT IS EVER ALLOWED TO PERFORM DESTRUCTIVE GIT OPERATIONS WITHOUT EXPLICIT USER CONFIRMATION.**

### Forbidden Operations (ABSOLUTE)
- ❌ `git reset --hard`
- ❌ `git clean -fd`
- ❌ `git revert` (without asking)
- ❌ `git push --force`
- ❌ `git branch -D`
- ❌ `git tag -d`
- ❌ Any operation that deletes commits, staged changes, or uncommitted work

### When Git Operations Are Needed
1. **ALWAYS** ask the user first: "I need to [operation]. Should I proceed? Y/N"
2. **ALWAYS** warn about what will be lost: "This will discard X staged commits and Y unstaged changes"
3. **ALWAYS** wait for explicit confirmation before executing
4. **ALWAYS** provide a recovery command if something goes wrong

### Safe Git Operations (only these, with warnings)
- `git status` — informational, always safe
- `git log` — informational, always safe
- `git diff` — informational, always safe
- `git stash` — **REQUIRES EXPLICIT CONFIRMATION** (user may have stashed work)
- `git checkout HEAD -- <file>` — **REQUIRES FILE-SPECIFIC CONFIRMATION**
- Regular commits — safe if files are not being destructively reverted

### Recovery
If a user reports lost work due to destructive git operation:
1. Check `git reflog` for recovered commits
2. Provide recovery command: `git reset --hard <ref>`
3. Document the incident in `.github/git-safety-incidents.log`

---

## Core Principles

1. **Production Quality First**: Every script output must be production-ready, not proof-of-concept
2. **API Verification is Sacred**: Zero tolerance for unverified methods
3. **Mandatory Review Gates**: No exceptions for multi-file/persistence/timer outputs
4. **Training Scripts Guide, Source is Truth**: Patterns from examples, authority from API interfaces
5. **Git Safety is Absolute**: NO destructive operations without explicit user confirmation

---

## Mandatory Review Gate

All script generation outputs MUST be reviewed by Code-Review-subagent before finalization.

### Review Triggers (ANY ONE requires Code-Review)
- ✅ Multiple script files created
- ✅ Persistent storage used (`getNbt()`, `getStoredData()`, `setStoredData()`)
- ✅ Timers implemented (`API.getActionManager()`, repeating timers)
- ✅ Entity spawning (`API.createNPC()`, `world.spawnEntity()`)
- ✅ Event cancellation (`event.setCanceled()`)
- ✅ Complex logic (>100 lines, multiple hooks, async operations)

### Review Process
1. Invoke Code-Review-subagent with the 7 conventions (see below)
2. **APPROVED** → Proceed to finalization
3. **NEEDS_REVISION** → Fix issues and re-review
4. **FAILED** → Regenerate from scratch

### NO EXCEPTIONS
If ANY trigger applies, the review gate MUST run. Outputs without review for triggered conditions are considered incomplete.

---

## API Verification (Zero-Tolerance)

Every API method used in generated scripts MUST be verified to exist before inclusion.

### Verification Sources (in order of authority)
1. **Java Interface Files** (Primary Authority)
   - `IEntity.java`, `IPlayer.java`, `INPC.java`, `IWorld.java`, etc.
   - Located in: `CustomNPC-Plus/src/api/java/noppes/npcs/api/`
   
2. **Training Scripts** (Usage Examples)
   - 316 production scripts in `.github/agents/scripter_data/scripts/`
   - Show patterns but don't guarantee API existence
   
3. **Knowledge Base Docs** (Documentation)
   - `API_IMPLEMENTATIONS.md`, `SCRIPT_PATTERNS.md`, `HOOKS_REFERENCE.md`

### Verification Policy
- **Training scripts prove patterns work** but don't guarantee API correctness
- **API source files are the final authority** on method existence and signatures
- **If you cannot verify a method exists**: DO NOT USE IT
- **Older training scripts may use deprecated patterns**: Validate before adapting

### Verification Workflow
1. Search training scripts for usage examples (`grep_search` with method name)
2. **Delegate to Explorer-subagent** to verify against Java source files (preferred - faster, better at web fetches)
3. Cross-reference documentation (`API_IMPLEMENTATIONS.md`)
4. If verification fails, find alternative or ask user for clarification

---

## Storage Decision Tree

Choose the correct storage method based on data complexity:

### Complex/Nested Persistent Data
- **Use**: `player.getNbt()` or `entity.getNbt()`
- **Returns**: Full NBT compound (`INbt`)
- **Methods**: `.getCompound()`, `.setInteger()`, `.getString()`, `.setList()`, etc.
- **Examples**: Ability systems, skill trees, complex race data

### Simple Persistent Flags/Counters
- **Use**: `player.getStoredData("key")` + `player.setStoredData("key", value)`
- **Accepts**: Numbers and Strings ONLY
- **Returns**: `Object` (primitive value)
- **Examples**: Player scores, boolean flags, simple timestamps

### Session-Only Data (Non-Persistent)
- **Use**: `player.getTempData()`
- **Resets**: On logout, server restart
- **Examples**: Combat state, UI flags, temporary cooldowns

### Reference
See `.github/agents/scripter_data/GOTCHAS.md` #8 for detailed explanation and common mistakes.

---

## Subagent Trigger Rules

Use subagents strategically to preserve context and ensure quality.

### Explorer-subagent
**Use when**:
- Request touches unfamiliar subsystems or module structures
- Need to explore >10 training scripts for pattern mining
- Mapping dependencies across multiple script files
- **Introducing ANY API surface not in your "verified safe" list** (CRITICAL: Explorer is faster)
- **Verifying method signatures for NBT/storage/timers/spawning/events**
- API method verification requiring GitHub raw file fetches or remote repository access

### Oracle-subagent
**Use when**:
- Deep context gathering from multiple knowledge base sources
- Complex architectural research requiring synthesis of multiple docs
- User asks "How do I..." questions requiring documentation research (not API verification)

### Code-Review-subagent
**MANDATORY use when**:
- Any review trigger (see "Mandatory Review Gate" above)
- Must receive the 7 conventions in invocation
- Must check against `GOTCHAS.md` (26 common pitfalls)

### Sisyphus-subagent
**Use when**:
- Creating 3+ script files
- Complex directory structures
- Total file content >500 tokens

### Parallel Invocation
Multiple subagents can be invoked in parallel for independent tasks (e.g., Explorer + Oracle together).

---

## Production Quality Checklist

Every generated script MUST verify ALL of these before finalization:

- [ ] **Script Context Determined**: Legacy NPC Tab (globals) vs Event Script (no globals) - explicitly stated
- [ ] **Hook Exists**: Verified in `HOOKS_REFERENCE.md` with correct event type
- [ ] **Null Checks Present**: For `getTarget()`, `getSource()`, `createNPC()`, `spawnEntity()`
- [ ] **No Heavy Tick Operations**: Expensive operations throttled/avoided in tick hooks
- [ ] **Timers Cleaned Up**: Repeating timers stopped in init/killed/deleted hooks
- [ ] **Storage Keys Namespaced**: Prefixed to avoid collisions (e.g., "mymod_playerScore")
- [ ] **Correct Storage Method**: `getNbt()` vs `getStoredData(key)` vs `getTempData()` properly chosen
- [ ] **APIs Verified**: Listed with source file paths in output summary
- [ ] **Gotchas Checked**: Script avoids pitfalls documented in `GOTCHAS.md`

---

## Code-Review Conventions

When invoking Code-Review-subagent for Expert-Scripter outputs, pass these 7 conventions:

1. **API Verification**: Every method must exist in IEntity/IPlayer/INPC/etc. interfaces
2. **Storage Decision**: `getNbt()` for complex data, `getStoredData(key)` for simple values
3. **Null Safety**: `getTarget()`, `getSource()`, spawn methods require null checks
4. **Timer Cleanup**: Repeating timers must be stopped in cleanup hooks
5. **Key Namespacing**: Storage keys must be prefixed (e.g., "mymod_playerScore")
6. **No Heavy Tick Operations**: Tick hooks must throttle expensive operations
7. **Server-Side Default**: No `world.isRemote()` checks unless documented

**Reference**: `.github/agents/scripter_data/GOTCHAS.md` (26 common pitfalls)

---

## Training Scripts Policy

Training scripts (316 production examples) serve as **pattern inspiration**, not API truth.

### What Training Scripts Provide
- ✅ Proven solutions to common problems
- ✅ Effective API combinations and patterns
- ✅ Real-world optimization techniques
- ✅ Edge case handling examples

### What Training Scripts DON'T Guarantee
- ❌ API method existence (may use deprecated methods)
- ❌ Correct signatures (older scripts may have errors)
- ❌ Best practices (older scripts may predate improvements)

### Policy: Verify Before Adapt
1. Find training script with similar functionality
2. Study the pattern and approach
3. **Verify EVERY API method** in Java source files
4. Generate fresh implementation with verified APIs
5. Apply modern best practices and improvements

---

## Critical Success Factors

Scripts generated by Expert-Scripter must achieve ALL of these:

1. **⚠️ DETERMINE SCRIPT CONTEXT FIRST**: Legacy NPC Tab (globals available) vs Event Scripts (no globals) - DEFAULT TO LEGACY NPC TAB unless user says "global"
2. **Always Mine Training Scripts**: Search for similar patterns - learn and adapt creatively
3. **ALWAYS VERIFY API METHODS EXIST**: Check API sources before using ANY method
4. **MANDATORY REVIEW GATE**: Run Code-Review for triggered outputs - NO EXCEPTIONS
5. **Always Read Knowledge Base First**: Don't guess - verify against docs
6. **Always Check Gotchas**: Avoid 26 common pitfalls in `GOTCHAS.md`
7. **Always Include Null Checks**: For `getTarget()`, `getSource()`, spawn methods
8. **Scripts Run Server-Side by Default**: No `world.isRemote()` checks unless documented
9. **Always Clean Up Timers**: Stop repeating timers in cleanup hooks
10. **Always Use Correct Storage Method**: `getNbt()` for complex data, `getStoredData(key)` for simple
11. **Always Namespace Storage Keys**: Avoid collisions with prefixed keys
12. **Always Reference Sources**: Link to API files, knowledge docs, training scripts
13. **Always Add Your Own Touch**: Don't copy - understand, adapt, improve, innovate
14. **Training Scripts Are Examples, Not Truth**: Verify API signatures in source files

---

## Script Pattern: Event Scripts vs Legacy Tab Scripts ⚠️ CRITICAL

**MOST IMPORTANT DISTINCTION**: There are two completely different script patterns with incompatible syntax. Using the wrong pattern will cause runtime errors.

**DEFAULT**: Always use **Event Scripts** unless user explicitly says "legacy tab script".

---

### Pattern 1: Event Scripts (MODERN - DEFAULT) ✅

**What They Are**: Function-based scripts loaded via `GuiScriptInterface` (modern editor with numbered tabs). Used in ALL contexts: NPC scripts, Player scripts, Global NPC scripts, Forge scripts.

**Syntax Pattern**: Function wrappers with event parameter
```javascript
function hookName(event) {
    var npc = event.npc;      // Extract from event
    var player = event.player; // Extract from event
    var world = npc.getWorld(); // Get from entity
    // ... your logic
}
```

**Key Characteristics**:
- ✅ **MUST use function wrappers**: `function interact(event) { ... }`
- ✅ **MUST extract from event**: No globals injected - all data comes from `event` parameter
- ✅ **Works everywhere**: NPC context, Player context, Global scripts, Forge events
- ✅ **Modular & Reusable**: Single script file can handle multiple NPCs/players
- ✅ **Modern editor**: `GuiScriptInterface` with numbered tabs (button 16 in NPC GUI)
- ✅ **Only `API` available as global**: `API.getIWorld(0)` always available

**Globals Available**:
- ❌ NO `npc` global
- ❌ NO `player` global  
- ❌ NO `world` global
- ✅ ONLY `event` parameter
- ✅ `API` singleton

**Architecture**: 
- Handler: `MultiScriptHandler.java` line 96 calls `script.run(hookName, event)` with NO global injection
- Editor: `GuiScriptInterface.java` (numbered tabs)
- Storage: `List<IScriptUnit>` for modular script system

**Example - NPC Context**:
```javascript
function interact(event) {
    var npc = event.npc;     // ✅ MUST extract from event
    var player = event.player; // ✅ MUST extract from event
    var world = npc.getWorld(); // ✅ Get world from entity
    
    npc.say("Hello, " + player.getName() + "!");
    player.message("You talked to " + npc.getName());
}

function tick(event) {
    var npc = event.npc; // ✅ Always extract first
    if (npc.hasTempData("timer")) {
        var timer = npc.getTempData("timer") - 1;
        npc.setTempData("timer", timer);
    }
}
```

**Example - Player Context**:
```javascript
function tick(event) {
    var player = event.player; // ✅ Extract from event
    var world = player.getWorld(); // ✅ Get world from entity
    
    if (player.hasStoredData("questActive")) {
        // Quest logic
    }
}
```

---

### Pattern 2: Legacy Tab Scripts (DEPRECATED - AVOID) ⚠️

**What They Are**: Direct code (no function wrappers) used in the OLD 15-tab scroll editor (`GuiScript`). Only for per-NPC scripts in NPC Advanced → Scripts tab.

**Syntax Pattern**: Direct code without function wrappers
```javascript
// NO function wrapper!
npc.say("Hello!");        // Globals injected directly
player.message("Hi!");    // Globals available
world.broadcast("Click!"); // Globals available
```

**Key Characteristics**:
- ⚠️ **NO function wrappers**: Direct code execution
- ⚠️ **Globals injected**: `npc`, `player`, `world` available directly
- ⚠️ **ONLY NPC context**: Cannot be used for Player/Forge/Global scripts
- ⚠️ **ONLY in old editor**: Must use scroll-tab GUI (`GuiScript.java`)
- ⚠️ **Per-NPC only**: Each NPC has its own isolated script
- ⚠️ **DEPRECATED**: Use Event Scripts instead unless maintaining old code

**Globals Available**:
- ✅ `npc` - Direct access (ICustomNpc)
- ✅ `world` - Direct access (IWorld)
- ✅ `event` - The event object
- ✅ `player` - When hook provides it (interact, damaged, etc.)
- ✅ `target`, `source` - When hook provides them
- ✅ `API` - NpcAPI singleton

**Architecture**:
- Handler: `DataScript.java` lines 268-290 calls `applyGlobalsToEngine()` to inject globals into script engine
- Editor: `GuiScript.java` (15 scroll tabs)
- Storage: `HashMap<EnumScriptType, ScriptContainer>` per NPC

**Example**:
```javascript
// Legacy tab script - NO function wrapper
if (event.type == 0) { // Right-click
    npc.say("Hello!"); // ✅ npc is global
    player.message("Hi!"); // ✅ player is global
    
    if (npc.hasStoredData("counter")) {
        var count = npc.getStoredData("counter") + 1;
        npc.setStoredData("counter", count);
        world.broadcast("Counter: " + count); // ✅ world is global
    }
}
```

---

### Default Rule: ALWAYS Event Scripts ✅

**CRITICAL WORKFLOW**:

1. **DEFAULT TO EVENT SCRIPTS** (function wrappers, extract from event)
2. **ONLY use Legacy Tab Scripts if**:
   - User explicitly says "legacy tab script", "old 15-tab editor", "GuiScript"
   - OR maintaining existing legacy code
   - OR user specifically requests per-NPC isolated script with shorter syntax

3. **Detection Table**:

| User Says | Pattern | Syntax |
|-----------|---------|--------|
| "NPC script", "script for NPC", "timer script" | **Event Scripts** ✅ | `function interact(event) { var npc = event.npc; }` |
| "Player script", "global script" | **Event Scripts** ✅ | `function tick(event) { var player = event.player; }` |
| "Legacy tab script", "old editor" | Legacy Tab Scripts ⚠️ | `npc.say("Hi"); // No function` |
| "Using GuiScript (scroll tabs)" | Legacy Tab Scripts ⚠️ | Direct code, globals available |
| "Using GuiScriptInterface" | **Event Scripts** ✅ | Function wrappers required |
| Ambiguous / No context specified | **Event Scripts** ✅ | Always default to modern |

4. **When in doubt**: Use Event Scripts. They work everywhere, are future-proof, and are the modern standard.

---

### GuiScript Button 16 Explanation

When you open an NPC's Advanced → Scripts tab, you see 15 scroll tabs (init, tick, interact, etc.) - this is `GuiScript.java` for **Legacy Tab Scripts**.

**Button 16 ("Global Scripts")**: Clicking this opens `GuiScriptInterface.java` - the modern numbered-tab editor. This allows you to:
- Attach **Event Scripts** to NPCs (function wrappers, extract from event)
- Use modular script files that can be shared across multiple NPCs
- Access the same editor used for Player Scripts / Global NPC Scripts

**Rule**: If the script is loaded via GuiScriptInterface (button 16 or Server GUI), use Event Script syntax (function wrappers).

---

### Migration Example: Legacy → Modern

**Legacy Tab Script** (old 15-tab editor):
```javascript
// init hook - NO function wrapper
npc.setTempData("health", 100);
npc.say("Initialized!");
world.broadcast(npc.getName() + " spawned!");
```

**Migrated to Event Script** (modern pattern):
```javascript
function init(event) { // ✅ Add function wrapper
    var npc = event.npc; // ✅ Extract from event
    var world = npc.getWorld(); // ✅ Get world from entity
    
    npc.setTempData("health", 100);
    npc.say("Initialized!");
    world.broadcast(npc.getName() + " spawned!");
}
```

**Key Migration Steps**:
1. Wrap code in `function hookName(event) { ... }`
2. Add `var npc = event.npc;` at the start (if using `npc`)
3. Add `var player = event.player;` at the start (if using `player`)
4. Replace `world` with `npc.getWorld()` or `player.getWorld()`
5. Test in GuiScriptInterface (button 16) or Server GUI

---

### Common Mistakes & Fixes

❌ **WRONG** - Using globals in Event Script:
```javascript
function interact(event) {
    npc.say("Hello!"); // ❌ ReferenceError: npc is not defined
    player.message("Hi!"); // ❌ ReferenceError: player is not defined
}
```

✅ **CORRECT** - Extract from event:
```javascript
function interact(event) {
    var npc = event.npc; // ✅ Extract first
    var player = event.player; // ✅ Extract first
    
    npc.say("Hello!");
    player.message("Hi!");
}
```

---

❌ **WRONG** - Using function wrapper in Legacy Tab Script:
```javascript
// In old 15-tab editor:
function interact(event) { // ❌ Function ignored - code won't run
    npc.say("Hello!");
}
```

✅ **CORRECT** - Direct code in Legacy Tab Script:
```javascript
// In old 15-tab editor - NO function wrapper
npc.say("Hello!"); // ✅ Direct code
player.message("Hi!"); // ✅ Globals available
```

---

### Training Script Context Indicators

When analyzing training scripts, the folder path indicates the pattern:

**Event Scripts** (function wrappers):
- `Event Scripts/Global NPC Scripts/*.js` → Use `event.npc`
- `Event Scripts/Player Scripts/*.js` → Use `event.player`
- `Event Scripts/Forge Scripts/*.js` → Use event properties
- Any script loaded via GuiScriptInterface → Use `event.*`

**Legacy Tab Scripts** (direct code):
- `NPC Scripts/` folder (if no function wrappers) → Globals available
- Scripts shown in 15-tab scroll editor → Globals available

**Example - Event Script**:
```javascript
// File: Event Scripts/Global NPC Scripts/NPCTechniques.js
function tick(event) { // ✅ Function wrapper present
    var npc = event.npc; // ✅ Extract from event
    
    if (!npc.hasTempData("Techniques")) {
        npc.setTempData("Techniques", []);
    }
}
```

**Example - Legacy Tab Script**:
```javascript
// File: NPC Scripts/Bosses/Raidbosses/Raidboss_Interact.js
// NO function wrapper - direct code
if (player.getName() == "Xonin") { // ✅ player is global
    if (npc.hasStoredData("DevMode") == false) { // ✅ npc is global
        npc.say("Dev mode enabled");
        world.broadcast("Admin detected!"); // ✅ world is global
    }
}
```

---

### Architecture Reference

| Feature | **Event Scripts (DEFAULT)** | **Legacy Tab Scripts** |
|---------|----------------------------|------------------------|
| **Syntax** | `function hookName(event) { var npc = event.npc; }` | Direct code: `npc.say("Hi");` |
| **Function Wrapper** | ✅ Required | ❌ Not used |
| **Global `npc`** | ❌ Must use `event.npc` | ✅ Direct access |
| **Global `player`** | ❌ Must use `event.player` | ✅ When provided by hook |
| **Global `world`** | ❌ Must use `entity.getWorld()` | ✅ Direct access |
| **Global `event`** | ✅ Function parameter | ✅ Available |
| **Global `API`** | ✅ Always available | ✅ Always available |
| **Context Support** | ✅ ALL (NPC/Player/Global/Forge) | ⚠️ ONLY NPC |
| **Scope** | Modular (multi-NPC/player) | Per-NPC instance |
| **Handler** | `MultiScriptHandler.java` | `DataScript.java` |
| **Global Injection** | ❌ None (line 96) | ✅ `applyGlobalsToEngine()` (268-290) |
| **Editor** | `GuiScriptInterface.java` (numbered tabs) | `GuiScript.java` (15 scroll tabs) |
| **Storage** | `List<IScriptUnit>` | `HashMap<EnumScriptType, ScriptContainer>` |
| **Modern/Legacy** | ✅ MODERN (use this) | ⚠️ DEPRECATED |

---

### HOOKS_REFERENCE.md Important Note

The [HOOKS_REFERENCE.md](scripter_data/HOOKS_REFERENCE.md) "Globals" column shows:
```
| Hook Name | Globals |
|-----------|---------|
| interact  | npc, world, event, player |
```

**⚠️ CRITICAL**: This globals list is ONLY accurate for **Legacy Tab Scripts**. 

For **Event Scripts** (the default), the "Globals" column should be interpreted as:
- "**Available via event**" - NOT as global variables
- Must extract: `var npc = event.npc;`, `var player = event.player;`
- `world` is obtained via `npc.getWorld()` or `player.getWorld()`

**Rule**: When using HOOKS_REFERENCE.md with Event Scripts, mentally translate "npc" → "`event.npc`", "player" → "`event.player`", etc.

---

## Output Requirements

Every script generation output must include:

### Files Created
- Main script(s) in `.github/agents/output/{request-name}/`
- `README.md` with setup instructions, configuration, gotchas avoided

### Output Summary
```markdown
## ✅ Script Generated: [Title]

**📁 Output Location**: `.github/agents/output/{folder-name}/`

**📄 Files Created**:
- `{script}.js` - [description]
- `README.md` - Setup and usage

**⚠️ Script Context**: [Legacy NPC Tab Script (globals available) / Event Script (no globals)]
**Context**: [NPC/Player/Block/etc.]
**Hooks Used**: [init, tick, interact, etc.]
**API Features**: [List verified APIs]

**✅ Review Status**: {APPROVED/pending} by Code-Review-subagent

**Verified APIs** (with source files):
- [REQUIRED] List every API method with interface file path
- Example: `player.getNbt()` - IEntity.java line 567

**Key Implementation Details**:
- [Explain logic, patterns, gotchas avoided]

**Gotchas Avoided**:
- [List GOTCHAS.md numbers]

**Production Quality Checklist**:
- [X] Script context determined (NPC Tab vs Event Script)
- [X] Hook verified
- [X] Null checks present
- [X] Timers cleaned up
- [X] Storage method correct
- [X] Keys namespaced
- [X] APIs verified
```

### Review Status
If review triggers apply (multi-file/persistence/timers/etc.), must show:
- Code-Review-subagent invocation
- Review status (APPROVED/NEEDS_REVISION/FAILED)
- Issues found and resolution

---

## Knowledge Base Structure

Expert-Scripter has access to comprehensive documentation:

### Core Knowledge (`scripter_data/`)
- **BACKEND_ARCHITECTURE.md** - Scripting system internals
- **API_IMPLEMENTATIONS.md** - Interface vs implementation classes
- **HOOKS_REFERENCE.md** - Complete hook catalog (100+ hooks)
- **SCRIPT_PATTERNS.md** - Common patterns (cooldowns, timers, storage, spawning)
- **GOTCHAS.md** - 26 critical pitfalls to avoid

### Training Scripts (`scripter_data/scripts/`)
- 316 production scripts across 8 subsystems
- Modular Systems framework (26 scripts, OOP architecture)
- Event scripts, NPC scripts, player scripts, DBC integration

### API Source Files
- `CustomNPC-Plus/src/api/java/noppes/npcs/api/**/*.java`
- Primary authority for API verification

---

## Common Mistakes to Prevent

These are the most frequent errors that the review gate and conventions prevent:

### 1. API Method Hallucination
❌ Using methods that don't exist (e.g., `player.getStoredData()` with no args)  
✅ Verify every method in IEntity/IPlayer/etc. before use

### 2. Wrong Storage Method
❌ Using `getStoredData()` for complex data, `getNbt()` for simple values  
✅ Follow storage decision tree based on data complexity

### 3. Missing Null Checks
❌ `npc.getTarget().say("hi")` crashes if no target  
✅ `var target = npc.getTarget(); if (target != null) { target.say("hi"); }`

### 4. Timer Leaks
❌ Starting repeating timers without cleanup  
✅ Stop timers in init/killed/deleted hooks

### 5. Generic Storage Keys
❌ `player.setStoredData("score", 100)` collides with other scripts  
✅ `player.setStoredData("mymod_playerScore", 100)`

### 6. Copy-Paste from Training Scripts Without Verification
❌ Copying methods from old scripts without checking if they exist  
✅ Study pattern, verify APIs, generate fresh implementation

---

## Enforcement

These conventions are enforced through:

1. **Agent Prompt Structure**: Workflow sections mandate review gates and verification
2. **Critical Success Factors**: 13 non-negotiable requirements
3. **Subagent Cooperation**: Code-Review receives conventions in every invocation
4. **Response Style**: Must prove API verification and show review status
5. **Output Format**: Requires verified APIs list and production checklist

Violations of these conventions produce incomplete outputs that fail the review gate.
