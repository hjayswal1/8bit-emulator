import React, { useState } from 'react';

// Fake data to visualize the CPU before we connect Java
const mockData = {
  registers: { A: "0x00", B: "0x1A", C: "0xFF", IR: "0x20", PC: "0x04" },
  memory: Array.from({ length: 32 }).fill("0x00")
};

export default function CpuEmulator() {
  const [cpuState, setCpuState] = useState(mockData); 

  return (
    <div className="h-screen w-full bg-[#0D1117] text-[#E6EDF3] font-mono flex flex-col">
      
      {/* Top Navbar / Control Panel */}
      <header className="h-16 border-b border-gray-800 flex items-center justify-between px-6">
        <h1 className="text-xl font-bold tracking-widest text-cyan-400">
          8-BIT CORE <span className="text-gray-500 text-sm">v1.0</span>
        </h1>
        <div className="flex gap-4">
          <button className="px-6 py-2 bg-green-900/40 text-green-400 border border-green-700 rounded hover:bg-green-800/60 transition shadow-[0_0_10px_rgba(74,222,128,0.2)]">
            ▶ RUN
          </button>
          <button className="px-6 py-2 bg-yellow-900/40 text-yellow-400 border border-yellow-700 rounded hover:bg-yellow-800/60 transition">
            ⏭ STEP
          </button>
          <button className="px-6 py-2 bg-red-900/40 text-red-400 border border-red-700 rounded hover:bg-red-800/60 transition">
            ⏹ RESET
          </button>
        </div>
      </header>

      {/* Main Three-Pane Workspace */}
      <main className="flex-1 grid grid-cols-12 gap-6 p-6 overflow-hidden">
        
        {/* Left: Code Editor (3 cols) */}
        <section className="col-span-3 border border-gray-800 rounded-lg bg-[#161B22] p-4 flex flex-col shadow-lg shadow-black/50">
          <h2 className="text-xs text-gray-500 mb-4 uppercase tracking-widest font-bold">Assembly Source</h2>
          <textarea 
            className="flex-1 bg-transparent resize-none outline-none text-sm text-cyan-100 leading-relaxed"
            defaultValue={"MOV A, 0x1A\nADD B\nHLT"}
            spellCheck="false"
          />
        </section>

        {/* Center: CPU Architecture & Registers (6 cols) */}
        <section className="col-span-6 border border-gray-800 rounded-lg bg-[#161B22] p-6 flex flex-col items-center justify-center relative shadow-lg shadow-black/50">
          <h2 className="absolute top-4 left-4 text-xs text-gray-500 uppercase tracking-widest font-bold">CPU Architecture</h2>
          
          <div className="flex gap-8">
            {/* Accumulator A */}
            <div className="w-32 border border-gray-700 bg-[#0D1117] rounded p-3 shadow-inner">
              <div className="text-[10px] text-gray-500 mb-1 tracking-wider">REG A</div>
              <div className="text-3xl text-green-400 text-center font-bold tracking-widest">{cpuState.registers.A}</div>
            </div>

            {/* Register B */}
            <div className="w-32 border border-gray-700 bg-[#0D1117] rounded p-3 shadow-inner">
              <div className="text-[10px] text-gray-500 mb-1 tracking-wider">REG B</div>
              <div className="text-3xl text-green-400 text-center font-bold tracking-widest">{cpuState.registers.B}</div>
            </div>
          </div>

          {/* Program Counter */}
          <div className="mt-12 w-48 border border-gray-700 bg-[#0D1117] rounded p-3 relative">
            <div className="absolute -top-3 left-1/2 -translate-x-1/2 bg-[#161B22] px-2 text-[10px] text-cyan-500 tracking-wider">PROGRAM COUNTER</div>
            <div className="text-3xl text-cyan-400 text-center font-bold tracking-widest">{cpuState.registers.PC}</div>
          </div>
          
        </section>

        {/* Right: RAM / Memory Grid (3 cols) */}
        <section className="col-span-3 border border-gray-800 rounded-lg bg-[#161B22] p-4 flex flex-col shadow-lg shadow-black/50">
          <h2 className="text-xs text-gray-500 mb-4 uppercase tracking-widest font-bold">Memory Map (RAM)</h2>
          <div className="flex-1 overflow-y-auto grid grid-cols-4 gap-2 content-start pr-2">
            {cpuState.memory.map((val, i) => (
              <div key={i} className="text-center py-2 border border-gray-800 rounded text-xs text-gray-500 hover:border-cyan-500 hover:text-cyan-400 cursor-pointer transition bg-[#0D1117]">
                {val}
              </div>
            ))}
          </div>
        </section>

      </main>
    </div>
  );
}