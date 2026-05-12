document.addEventListener("DOMContentLoaded", () => {
    const runBtn = document.getElementById("btn-run");
    const stepBtn = document.getElementById("btn-step");
    const resetBtn = document.getElementById("btn-reset");
    const codeEditor = document.getElementById("code-editor");
    const toast = document.getElementById("toast");
    
    // Generate an empty memory grid (First 16 addresses for preview)
    const memBody = document.getElementById("memory-body");
    
    function renderMemory(memoryMap = {}) {
        memBody.innerHTML = "";
        for (let i = 0; i < 32; i++) {
            const hexAddr = "0x" + i.toString(16).padStart(4, "0").toUpperCase();
            const val = memoryMap[hexAddr] || "00";
            
            const tr = document.createElement("tr");
            tr.className = "hover:bg-gray-800 border-b border-gray-800 transition-colors";
            tr.innerHTML = `
                <td class="py-1 px-4 font-bold text-gray-500">${hexAddr}</td>
                <td class="py-1 px-4 text-white">${val}</td>
            `;
            memBody.appendChild(tr);
        }
    }
    
    renderMemory();

    // Send instructions to the Java Backend
    async function sendCodeToBackend(code, isStep = false, isReset = false) {
        try {
            // UPDATE THIS URL to point to your Java Spring Boot / Servlet controller
            const response = await fetch("http://localhost:8080/api/cpu/execute", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ 
                    instructions: code, 
                    stepMode: isStep,
                    reset: isReset
                })
            });
            
            if (response.ok) {
                const cpuState = await response.json();
                updateUI(cpuState);
            } else {
                showToast("Server returned an error. Check your Assembly syntax.");
            }
        } catch (error) {
            console.error("Backend connection error:", error);
            showToast("Failed to connect. Is your Java backend running on localhost:8080?");
        }
    }

    // Updates the visual Dashboard
    function updateUI(state) {
        // Expecting JSON shape: { registers: { A: "0x1E", B: "0x0A", ... }, flags: "Z=0...", memory: { "0x0000": "1A" } }
        if (state.registers) {
            document.getElementById("reg-a").innerText = state.registers.A || "0x00";
            document.getElementById("reg-b").innerText = state.registers.B || "0x00";
            document.getElementById("reg-c").innerText = state.registers.C || "0x00";
            document.getElementById("reg-d").innerText = state.registers.D || "0x00";
            document.getElementById("reg-pc").innerText = state.registers.PC || "0x0000";
            document.getElementById("reg-sp").innerText = state.registers.SP || "0xFFFF";
        }
        if (state.flags) {
            document.getElementById("reg-flags").innerText = state.flags;
        }
        if (state.memory) {
            renderMemory(state.memory);
        }
    }

    function showToast(message) {
        toast.innerText = message;
        toast.classList.remove("hidden");
        setTimeout(() => toast.classList.add("hidden"), 4000);
    }

    // Attach event listeners
    runBtn.addEventListener("click", () => sendCodeToBackend(codeEditor.value, false, false));
    stepBtn.addEventListener("click", () => sendCodeToBackend(codeEditor.value, true, false));
    resetBtn.addEventListener("click", () => sendCodeToBackend(codeEditor.value, false, true));
});