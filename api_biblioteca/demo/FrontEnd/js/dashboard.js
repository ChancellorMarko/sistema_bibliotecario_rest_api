// Dashboard - Sistema de Biblioteca MeAjuda
// Versão independente - n��o depende de config.js

;(() => {
  // Configurações da API
  const API_CONFIG = {
    baseUrl: "http://localhost:8080/api",
    endpoints: {
      livros: "/livros",
      clientes: "/clientes",
      emprestimos: "/emprestimos",
      reservas: "/reservas",
      multas: "/multas",
    },
  }

  // Função para fazer requisições à API
  async function makeApiRequest(endpoint, options = {}) {
    const url = API_CONFIG.baseUrl + endpoint
    const defaultOptions = {
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
      },
    }

    const requestOptions = { ...defaultOptions, ...options }

    try {
      console.log(`🔄 Fazendo requisição para: ${url}`)
      const response = await fetch(url, requestOptions)
      console.log(`📊 Status da resposta: ${response.status}`)

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`)
      }

      const data = await response.json()
      console.log("✅ Dados recebidos:", data)

      return data
    } catch (error) {
      console.error(`❌ Erro na requisição para ${url}:`, error.message)
      throw error
    }
  }

  // Função para mostrar alertas
  function displayAlert(message, type = "info") {
    console.log(`🔔 Alert [${type}]: ${message}`)

    const alertContainer = document.getElementById("alertContainer")
    if (!alertContainer) {
      console.warn("Container de alertas não encontrado")
      return
    }

    const alertElement = document.createElement("div")
    alertElement.className = `alert alert-${type}`
    alertElement.innerHTML = `
            <div>${message}</div>
            <button onclick="this.parentElement.remove()" style="background:none;border:none;color:inherit;cursor:pointer;float:right;">&times;</button>
        `

    alertContainer.appendChild(alertElement)

    // Auto-remover após 5 segundos
    setTimeout(() => {
      if (alertElement.parentElement) {
        alertElement.remove()
      }
    }, 5000)
  }

  // Função para formatar data
  function formatDate(dateString) {
    try {
      if (!dateString) return "Data não disponível"
      const date = new Date(dateString)
      if (isNaN(date.getTime())) return "Data inválida"

      return date.toLocaleDateString("pt-BR", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      })
    } catch (error) {
      console.error("Erro ao formatar data:", error)
      return "Data não disponível"
    }
  }

  // Função para atualizar estatística com animação
  function updateStatistic(elementId, value) {
    const element = document.getElementById(elementId)
    if (!element) {
      console.warn(`Elemento ${elementId} não encontrado`)
      return
    }

    console.log(`📈 Atualizando ${elementId}: ${value}`)

    // Remover loading
    element.classList.remove("loading")

    // Se for número, fazer animação de contagem
    if (!isNaN(value) && value >= 0) {
      let counter = 0
      const finalValue = Number.parseInt(value)
      const increment = Math.max(1, Math.ceil(finalValue / 20))

      const interval = setInterval(() => {
        counter += increment
        if (counter >= finalValue) {
          counter = finalValue
          clearInterval(interval)
        }
        element.textContent = counter
      }, 50)
    } else {
      element.textContent = value || "0"
    }
  }

  // Função para verificar se o backend está rodando
  async function checkBackendConnection() {
    try {
      console.log("🔍 Verificando conexão com o backend...")
      const response = await fetch(API_CONFIG.baseUrl.replace("/api", ""), {
        method: "HEAD",
        mode: "cors",
      })
      console.log("✅ Backend está respondendo")
      return true
    } catch (error) {
      console.error("❌ Backend não está respondendo:", error)
      displayAlert("Não foi possível conectar ao servidor. Verifique se o backend está rodando na porta 8080.", "error")
      return false
    }
  }

  // Função para carregar estatísticas
  async function loadStatistics() {
    console.log("🚀 Iniciando carregamento de estatísticas...")

    try {
      // Carregar total de livros
      console.log("📚 Carregando livros...")
      try {
        const livrosResponse = await makeApiRequest(API_CONFIG.endpoints.livros)

        if (livrosResponse && livrosResponse.success && Array.isArray(livrosResponse.data)) {
          updateStatistic("totalLivros", livrosResponse.data.length)
        } else if (livrosResponse && Array.isArray(livrosResponse)) {
          updateStatistic("totalLivros", livrosResponse.length)
        } else {
          console.warn("Resposta de livros em formato inesperado:", livrosResponse)
          updateStatistic("totalLivros", 0)
        }
      } catch (error) {
        console.error("❌ Erro ao carregar livros:", error)
        updateStatistic("totalLivros", "Erro")
      }

      // Carregar total de clientes
      console.log("👥 Carregando clientes...")
      try {
        const clientesResponse = await makeApiRequest(API_CONFIG.endpoints.clientes)

        if (clientesResponse && clientesResponse.success && Array.isArray(clientesResponse.data)) {
          updateStatistic("totalClientes", clientesResponse.data.length)
        } else if (clientesResponse && Array.isArray(clientesResponse)) {
          updateStatistic("totalClientes", clientesResponse.length)
        } else {
          console.warn("Resposta de clientes em formato inesperado:", clientesResponse)
          updateStatistic("totalClientes", 0)
        }
      } catch (error) {
        console.error("❌ Erro ao carregar clientes:", error)
        updateStatistic("totalClientes", "Erro")
      }

      // Carregar total de empréstimos
      console.log("📋 Carregando empréstimos...")
      try {
        const emprestimosResponse = await makeApiRequest(API_CONFIG.endpoints.emprestimos)

        if (emprestimosResponse && emprestimosResponse.success && Array.isArray(emprestimosResponse.data)) {
          updateStatistic("totalEmprestimos", emprestimosResponse.data.length)
          await loadRecentActivities(emprestimosResponse.data)
        } else if (emprestimosResponse && Array.isArray(emprestimosResponse)) {
          updateStatistic("totalEmprestimos", emprestimosResponse.length)
          await loadRecentActivities(emprestimosResponse)
        } else {
          console.warn("Resposta de empréstimos em formato inesperado:", emprestimosResponse)
          updateStatistic("totalEmprestimos", 0)
          await loadRecentActivities([])
        }
      } catch (error) {
        console.error("❌ Erro ao carregar empréstimos:", error)
        updateStatistic("totalEmprestimos", "Erro")
        await loadRecentActivities([])
      }

      // Carregar empréstimos atrasados
      console.log("⚠️ Carregando empréstimos atrasados...")
      try {
        const atrasadosResponse = await makeApiRequest(API_CONFIG.endpoints.emprestimos + "/atrasados")

        if (atrasadosResponse && atrasadosResponse.success && Array.isArray(atrasadosResponse.data)) {
          updateStatistic("totalAtrasados", atrasadosResponse.data.length)
        } else if (atrasadosResponse && Array.isArray(atrasadosResponse)) {
          updateStatistic("totalAtrasados", atrasadosResponse.length)
        } else {
          console.warn("Resposta de atrasados em formato inesperado:", atrasadosResponse)
          updateStatistic("totalAtrasados", 0)
        }
      } catch (error) {
        console.error("❌ Erro ao carregar empréstimos atrasados:", error)
        updateStatistic("totalAtrasados", "N/A")
      }

      console.log("✅ Estatísticas carregadas com sucesso!")
      displayAlert("Dashboard atualizado com sucesso!", "success")
    } catch (error) {
      console.error("❌ Erro geral ao carregar estatísticas:", error)
      displayAlert("Erro ao carregar dados do dashboard. Verifique se o backend está rodando.", "error")

      // Garantir que todos os loadings sejam removidos
      document.querySelectorAll(".stat-number.loading").forEach((el) => {
        el.classList.remove("loading")
        if (el.textContent === "-") {
          el.textContent = "Erro"
        }
      })
    }
  }

  // Função para carregar atividades recentes
  async function loadRecentActivities(emprestimos = null) {
    console.log("📋 Carregando atividades recentes...")

    const activityList = document.getElementById("activityList")
    if (!activityList) {
      console.warn("Lista de atividades não encontrada")
      return
    }

    try {
      // Se não foram passados empréstimos, tentar carregar
      if (!emprestimos) {
        try {
          const emprestimosResponse = await makeApiRequest(API_CONFIG.endpoints.emprestimos)
          if (emprestimosResponse && emprestimosResponse.success && emprestimosResponse.data) {
            emprestimos = emprestimosResponse.data
          } else if (Array.isArray(emprestimosResponse)) {
            emprestimos = emprestimosResponse
          } else {
            emprestimos = []
          }
        } catch (error) {
          console.error("Erro ao carregar empréstimos para atividades:", error)
          emprestimos = []
        }
      }

      activityList.innerHTML = ""

      if (emprestimos && emprestimos.length > 0) {
        console.log(`📊 ${emprestimos.length} empréstimos encontrados`)

        // Ordenar por ID decrescente (mais recentes primeiro)
        const emprestimosOrdenados = [...emprestimos]

        if (emprestimos[0] && emprestimos[0].dataEmprestimo) {
          emprestimosOrdenados.sort((a, b) => new Date(b.dataEmprestimo) - new Date(a.dataEmprestimo))
        } else if (emprestimos[0] && emprestimos[0].id) {
          emprestimosOrdenados.sort((a, b) => b.id - a.id)
        }

        // Pegar apenas os 5 mais recentes
        const emprestimosRecentes = emprestimosOrdenados.slice(0, 5)

        emprestimosRecentes.forEach((emprestimo) => {
          const li = document.createElement("li")
          li.className = "activity-item"

          // Determinar ícone e classe baseado no status
          let icon = "📚"
          let iconClass = "icon-loan"
          let statusText = "Empréstimo ativo"

          if (emprestimo.dataRetorno) {
            icon = "✅"
            iconClass = "icon-return"
            statusText = "Devolvido"
          } else if (emprestimo.status === "ATRASADO") {
            icon = "⚠️"
            iconClass = "icon-warning"
            statusText = "Atrasado"
          }

          // Formatar informações
          const livroInfo = emprestimo.livroId ? `Livro #${emprestimo.livroId}` : "Livro"
          const clienteInfo = emprestimo.clienteId ? `Cliente #${emprestimo.clienteId}` : "Cliente"

          let dataInfo = "Data não disponível"
          if (emprestimo.dataEmprestimo) {
            dataInfo = formatDate(emprestimo.dataEmprestimo)
          } else if (emprestimo.id) {
            dataInfo = `ID: ${emprestimo.id}`
          }

          li.innerHTML = `
                        <div class="activity-icon ${iconClass}">${icon}</div>
                        <div class="activity-content">
                            <div class="activity-title">${statusText}: ${livroInfo} para ${clienteInfo}</div>
                            <div class="activity-time">${dataInfo}</div>
                        </div>
                    `

          activityList.appendChild(li)
        })

        console.log("✅ Atividades carregadas com sucesso!")
      } else {
        console.log("ℹ️ Nenhum empréstimo encontrado")
        activityList.innerHTML = `
                    <li class="activity-item">
                        <div class="activity-icon icon-info">ℹ️</div>
                        <div class="activity-content">
                            <div class="activity-title">Nenhuma atividade recente</div>
                            <div class="activity-time">Comece a usar o sistema para ver atividades aqui</div>
                        </div>
                    </li>
                `
      }
    } catch (error) {
      console.error("❌ Erro ao carregar atividades recentes:", error)
      activityList.innerHTML = `
                <li class="activity-item">
                    <div class="activity-icon icon-error">⚠️</div>
                    <div class="activity-content">
                        <div class="activity-title">Erro ao carregar atividades</div>
                        <div class="activity-time">Verifique a conexão com o servidor</div>
                    </div>
                </li>
            `
    }
  }

  // Função para refresh manual
  function refreshStatistics() {
    console.log("🔄 Refresh manual das estatísticas...")

    // Mostrar loading novamente
    document.querySelectorAll(".stat-number").forEach((el) => {
      el.classList.add("loading")
      el.textContent = "-"
    })

    loadStatistics()
  }

  // Função de debug
  function debugDashboard() {
    console.log("🔧 === DEBUG DASHBOARD ===")
    console.log("API_CONFIG:", API_CONFIG)
    console.log("Elementos encontrados:")
    console.log("- totalLivros:", document.getElementById("totalLivros"))
    console.log("- totalClientes:", document.getElementById("totalClientes"))
    console.log("- totalEmprestimos:", document.getElementById("totalEmprestimos"))
    console.log("- totalAtrasados:", document.getElementById("totalAtrasados"))
    console.log("- activityList:", document.getElementById("activityList"))
    console.log("- alertContainer:", document.getElementById("alertContainer"))
    console.log("========================")
  }

  // Inicialização quando o DOM estiver carregado
  document.addEventListener("DOMContentLoaded", async () => {
    console.log("🚀 Dashboard carregado, iniciando verificações...")

    // Verificar se o backend está rodando
    const backendOk = await checkBackendConnection()

    if (backendOk) {
      // Carregar estatísticas iniciais
      await loadStatistics()

      // Configurar auto-refresh a cada 30 segundos
      setInterval(() => {
        console.log("🔄 Auto-refresh das estatísticas...")
        loadStatistics()
      }, 30000)
    } else {
      // Se o backend não estiver rodando, mostrar dados de exemplo
      console.log("📊 Mostrando dados de exemplo...")
      updateStatistic("totalLivros", "N/A")
      updateStatistic("totalClientes", "N/A")
      updateStatistic("totalEmprestimos", "N/A")
      updateStatistic("totalAtrasados", "N/A")

      document.getElementById("activityList").innerHTML = `
                <li class="activity-item">
                    <div class="activity-icon icon-error">🔌</div>
                    <div class="activity-content">
                        <div class="activity-title">Backend desconectado</div>
                        <div class="activity-time">Inicie o servidor Spring Boot na porta 8080</div>
                    </div>
                </li>
            `
    }

    // Adicionar event listeners para cards
    document.querySelectorAll(".stat-card").forEach((card) => {
      card.addEventListener("mouseenter", function () {
        this.style.transform = "translateY(-5px) scale(1.02)"
      })

      card.addEventListener("mouseleave", function () {
        this.style.transform = "translateY(0) scale(1)"
      })
    })
  })

  // Exportar funções para uso global
  window.refreshStatistics = refreshStatistics
  window.debugDashboard = debugDashboard
  window.loadStatistics = loadStatistics
})()
