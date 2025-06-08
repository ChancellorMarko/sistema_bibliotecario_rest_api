// Configuração da API
const API_BASE_URL = "http://localhost:8080/api"
const API_ENDPOINTS = {
  emprestimos: `${API_BASE_URL}/emprestimos`,
  emprestimosAtrasados: `${API_BASE_URL}/emprestimos/atrasados`,
}

// Inicialização quando a página carrega
document.addEventListener("DOMContentLoaded", () => {
  // Definir data padrão como hoje
  const hoje = new Date().toISOString().split("T")[0]
  document.getElementById("dataEmprestimo").value = hoje

  // Definir data prevista como 15 dias a partir de hoje
  const dataPrevista = new Date()
  dataPrevista.setDate(dataPrevista.getDate() + 15)
  document.getElementById("dataPrevistaDevolucao").value = dataPrevista.toISOString().split("T")[0]

  // Submit do formulário
  document.getElementById("emprestimoForm").addEventListener("submit", criarEmprestimo)
})

// Função para converter data para formato LocalDateTime
function formatDateTimeForAPI(dateString) {
  // Adiciona horário padrão (meio-dia) para evitar problemas de timezone
  return dateString + "T12:00:00"
}

// Função para criar empréstimo
async function criarEmprestimo(event) {
  event.preventDefault()

  const formData = new FormData(event.target)

  // Log dos dados do formulário para debug
  console.log("📝 Dados do formulário:")
  console.log("Cliente ID:", formData.get("clienteId"))
  console.log("Livro ID:", formData.get("livroId"))
  console.log("Data Empréstimo:", formData.get("dataEmprestimo"))
  console.log("Data Prevista:", formData.get("dataPrevistaDevolucao"))

 const emprestimoData = {
  clienteId: Number.parseInt(formData.get("clienteId")),
  livroId: Number.parseInt(formData.get("livroId")),
  dataEmprestimo: formatDateTimeForAPI(formData.get("dataEmprestimo")),
  dataDevolucao: formatDateTimeForAPI(formData.get("dataPrevistaDevolucao")),
  status: "EM_ANDAMENTO"
}

  console.log("📤 Dados sendo enviados para API:", emprestimoData)

  try {
    const response = await fetch(API_ENDPOINTS.emprestimos, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(emprestimoData),
    })

    console.log("📡 Status da resposta:", response.status)

    if (response.ok) {
      const result = await response.json()
      console.log("✅ Empréstimo criado:", result)
      alert("Empréstimo criado com sucesso!")
      document.getElementById("emprestimoForm").reset()

      // Redefinir datas padrão
      const hoje = new Date().toISOString().split("T")[0]
      document.getElementById("dataEmprestimo").value = hoje
      const dataPrevista = new Date()
      dataPrevista.setDate(dataPrevista.getDate() + 15)
      document.getElementById("dataPrevistaDevolucao").value = dataPrevista.toISOString().split("T")[0]
    } else {
      const errorData = await response.json()
      console.error("❌ Erro da API:", errorData)
      alert("Erro ao criar empréstimo: " + (errorData.message || errorData.error || "Erro desconhecido"))
    }
  } catch (error) {
    console.error("❌ Erro de rede:", error)
    alert("Erro de conexão com o servidor: " + error.message)
  }
}

// Função para listar todos os empréstimos
async function listarTodosEmprestimos() {
  try {
    console.log("🔍 Buscando todos os empréstimos...")
    const response = await fetch(API_ENDPOINTS.emprestimos)

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const result = await response.json()
    console.log("📋 Resposta da API:", result)

    // Extrair os dados do formato da API
    let emprestimos
    if (result.success && result.data) {
      emprestimos = result.data
    } else if (Array.isArray(result)) {
      emprestimos = result
    } else {
      emprestimos = []
    }

    console.log("📋 Empréstimos extraídos:", emprestimos)

    if (!Array.isArray(emprestimos)) {
      console.error("❌ Dados não são um array:", emprestimos)
      alert("Erro: Formato de resposta inválido da API")
      return
    }

    if (emprestimos.length === 0) {
      alert("ℹ️ Nenhum empréstimo encontrado")
      document.getElementById("listaEmprestimos").style.display = "none"
      return
    }

    exibirListaEmprestimos(emprestimos, "Todos os Empréstimos")
    document.getElementById("resultadoBusca").style.display = "none"
  } catch (error) {
    console.error("❌ Erro ao listar empréstimos:", error)
    alert("Erro de conexão com o servidor: " + error.message)
  }
}

// Função para listar empréstimos atrasados
async function listarEmprestimosAtrasados() {
  try {
    const response = await fetch(API_ENDPOINTS.emprestimosAtrasados)

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const result = await response.json()

    // Extrair os dados do formato da API
    let emprestimos
    if (result.success && result.data) {
      emprestimos = result.data
    } else if (Array.isArray(result)) {
      emprestimos = result
    } else {
      emprestimos = []
    }

    if (emprestimos.length === 0) {
      alert("ℹ️ Nenhum empréstimo atrasado encontrado")
      return
    }

    exibirListaEmprestimos(emprestimos, "Empréstimos Atrasados")
    document.getElementById("resultadoBusca").style.display = "none"
  } catch (error) {
    console.error("Erro ao listar empréstimos atrasados:", error)
    alert("Erro ao carregar empréstimos atrasados: " + error.message)
  }
}

// Função para buscar empréstimo por ID
async function buscarEmprestimo() {
  const id = document.getElementById("buscarEmprestimoId").value

  if (!id) {
    alert("Digite o ID do empréstimo")
    return
  }

  try {
    const response = await fetch(`${API_ENDPOINTS.emprestimos}/${id}`)

    if (!response.ok) {
      if (response.status === 404) {
        alert("Empréstimo não encontrado")
      } else {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      return
    }

    const emprestimo = await response.json()
    exibirDadosEmprestimo(emprestimo)
    document.getElementById("listaEmprestimos").style.display = "none"
  } catch (error) {
    console.error("Erro ao buscar empréstimo:", error)
    alert("Erro ao buscar empréstimo: " + error.message)
    document.getElementById("resultadoBusca").style.display = "none"
  }
}

// Função para buscar empréstimos por cliente
async function buscarEmprestimosPorCliente() {
  const clienteId = document.getElementById("buscarClienteId").value

  if (!clienteId) {
    alert("Digite o ID do cliente")
    return
  }

  try {
    console.log(`🔍 Buscando empréstimos do cliente ${clienteId}...`)
    const response = await fetch(`${API_ENDPOINTS.emprestimos}/cliente/${clienteId}`)

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const result = await response.json()
    console.log("📋 Resposta da API:", result)

    // Extrair os dados do formato da API
    let emprestimos
    if (result.success && result.data) {
      emprestimos = result.data
    } else if (Array.isArray(result)) {
      emprestimos = result
    } else {
      emprestimos = []
    }

    console.log("📋 Empréstimos do cliente extraídos:", emprestimos)

    if (emprestimos.length === 0) {
      alert(`ℹ️ Nenhum empréstimo encontrado para o cliente ${clienteId}`)
      return
    }

    exibirListaEmprestimos(emprestimos, `Empréstimos do Cliente ${clienteId}`)
    document.getElementById("resultadoBusca").style.display = "none"
  } catch (error) {
    console.error("❌ Erro ao buscar empréstimos por cliente:", error)
    alert("Erro ao buscar empréstimos do cliente: " + error.message)
  }
}

// Função para exibir lista de empréstimos
function exibirListaEmprestimos(emprestimos, titulo) {
  const container = document.getElementById("listaEmprestimos")
  const tabela = document.getElementById("tabelaEmprestimos").getElementsByTagName("tbody")[0]

  // Limpar tabela
  tabela.innerHTML = ""

  // Atualizar título
  container.querySelector("h3").textContent = `📋 ${titulo}`

  // Preencher tabela
  emprestimos.forEach((emprestimo) => {
    const row = tabela.insertRow()
    const statusClass = getStatusClass(emprestimo.status)
    const isDevolvido = emprestimo.dataDevolucao

    row.innerHTML = `
            <td>${emprestimo.id}</td>
            <td>${emprestimo.clienteId || emprestimo.cliente_id || "N/A"}</td>
            <td>${emprestimo.livroId || emprestimo.livro_id || "N/A"}</td>
            <td>${formatDate(emprestimo.dataEmprestimo)}</td>
            <td>${formatDate(emprestimo.dataPrevistaDevolucao || emprestimo.dataPrevista)}</td>
            <td>${emprestimo.dataDevolucao ? formatDate(emprestimo.dataDevolucao) : "Não devolvido"}</td>
            <td><span class="status ${statusClass}">${emprestimo.status || "ATIVO"}</span></td>
            <td class="actions">
                ${!isDevolvido ? `<button class="btn btn-success btn-sm" onclick="registrarDevolucao(${emprestimo.id})">Devolver</button>` : ""}
                <button class="btn btn-danger btn-sm" onclick="excluirEmprestimo(${emprestimo.id})">Excluir</button>
            </td>
        `
  })

  container.style.display = "block"
}

// Função para exibir dados de um empréstimo individual
function exibirDadosEmprestimo(emprestimo) {
  const container = document.getElementById("dadosEmprestimo")
  const isDevolvido = emprestimo.dataDevolucao

  container.innerHTML = `
        <div class="emprestimo-info">
            <p><strong>ID:</strong> ${emprestimo.id}</p>
            <p><strong>Cliente ID:</strong> ${emprestimo.clienteId || emprestimo.cliente_id}</p>
            <p><strong>Livro ID:</strong> ${emprestimo.livroId || emprestimo.livro_id}</p>
            <p><strong>Data do Empréstimo:</strong> ${formatDate(emprestimo.dataEmprestimo)}</p>
            <p><strong>Data Prevista para Devolução:</strong> ${formatDate(emprestimo.dataPrevistaDevolucao || emprestimo.dataPrevista)}</p>
            <p><strong>Data de Devolução:</strong> ${emprestimo.dataDevolucao ? formatDate(emprestimo.dataDevolucao) : "Não devolvido"}</p>
            <p><strong>Status:</strong> <span class="status ${getStatusClass(emprestimo.status)}">${emprestimo.status || "ATIVO"}</span></p>
            <div class="actions" style="margin-top: 1rem;">
                ${!isDevolvido ? `<button class="btn btn-success btn-sm" onclick="registrarDevolucao(${emprestimo.id})">Registrar Devolução</button>` : ""}
                <button class="btn btn-danger btn-sm" onclick="excluirEmprestimo(${emprestimo.id})">Excluir</button>
            </div>
        </div>
    `

  document.getElementById("resultadoBusca").style.display = "block"
}

// Função para registrar devolução
async function registrarDevolucao(id) {
  if (!confirm("Confirma a devolução deste livro?")) {
    return
  }

  try {
    const response = await fetch(`${API_ENDPOINTS.emprestimos}/${id}/devolver`, {
      method: "PATCH",
    })

    if (response.ok) {
      alert("Devolução registrada com sucesso!")
      // Atualizar lista se estiver visível
      if (document.getElementById("listaEmprestimos").style.display !== "none") {
        listarTodosEmprestimos()
      }

      // Atualizar busca individual se for o mesmo empréstimo
      if (document.getElementById("resultadoBusca").style.display !== "none") {
        buscarEmprestimo()
      }
    } else {
      const errorData = await response.json()
      alert("Erro ao registrar devolução: " + (errorData.message || "Erro desconhecido"))
    }
  } catch (error) {
    console.error("Erro ao registrar devolução:", error)
    alert("Erro de conexão com o servidor: " + error.message)
  }
}

// Função para excluir empréstimo
async function excluirEmprestimo(id) {
  if (!confirm("Tem certeza que deseja excluir este empréstimo?")) {
    return
  }

  try {
    const response = await fetch(`${API_ENDPOINTS.emprestimos}/${id}`, {
      method: "DELETE",
    })

    if (response.ok) {
      alert("Empréstimo excluído com sucesso!")
      // Atualizar lista se estiver visível
      if (document.getElementById("listaEmprestimos").style.display !== "none") {
        listarTodosEmprestimos()
      }

      // Limpar busca individual se for o mesmo empréstimo
      if (document.getElementById("resultadoBusca").style.display !== "none") {
        document.getElementById("resultadoBusca").style.display = "none"
      }
    } else {
      const errorData = await response.json()
      alert("Erro ao excluir empréstimo: " + (errorData.message || "Erro desconhecido"))
    }
  } catch (error) {
    console.error("Erro ao excluir empréstimo:", error)
    alert("Erro de conexão com o servidor: " + error.message)
  }
}

// Função para obter classe CSS do status
function getStatusClass(status) {
  switch (status) {
    case "DEVOLVIDO":
      return "status-devolvido"
    case "ATRASADO":
      return "status-atrasado"
    case "ATIVO":
    default:
      return "status-ativo"
  }
}

// Função para formatar data
function formatDate(dateString) {
  if (!dateString) return "N/A"

  try {
    const date = new Date(dateString)
    return date.toLocaleDateString("pt-BR")
  } catch (error) {
    return dateString
  }
}
