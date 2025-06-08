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

async function criarEmprestimo(event) {
  event.preventDefault()

  const formData = new FormData(event.target)
  const emprestimoData = {
    clienteId: Number.parseInt(formData.get("clienteId")),
    livroId: Number.parseInt(formData.get("livroId")),
    dataEmprestimo: formData.get("dataEmprestimo"),
    dataPrevistaDevolucao: formData.get("dataPrevistaDevolucao"),
  }

  try {
    const response = await apiRequest(API_ENDPOINTS.emprestimos, {
      method: "POST",
      body: JSON.stringify(emprestimoData),
    })

    if (response.data) {
      showAlert("Empréstimo criado com sucesso!", "success")
      document.getElementById("emprestimoForm").reset()

      // Redefinir datas padrão
      const hoje = new Date().toISOString().split("T")[0]
      document.getElementById("dataEmprestimo").value = hoje
      const dataPrevista = new Date()
      dataPrevista.setDate(dataPrevista.getDate() + 15)
      document.getElementById("dataPrevistaDevolucao").value = dataPrevista.toISOString().split("T")[0]
    } else if (response.error) {
      showAlert(response.error.message || "Erro ao criar empréstimo", "error")
    }
  } catch (error) {
    console.error("Erro ao criar empréstimo:", error)
    showAlert("Erro ao criar empréstimo", "error")
  }
}

async function listarTodosEmprestimos() {
  try {
    const response = await apiRequest(API_ENDPOINTS.emprestimos)

    if (response.data) {
      exibirListaEmprestimos(response.data, "Todos os Empréstimos")
      document.getElementById("resultadoBusca").style.display = "none"
    } else {
      showAlert("Nenhum empréstimo encontrado", "info")
    }
  } catch (error) {
    console.error("Erro ao listar empréstimos:", error)
    showAlert("Erro ao carregar lista de empréstimos", "error")
  }
}

async function listarEmprestimosAtrasados() {
  try {
    const response = await apiRequest(`${API_ENDPOINTS.emprestimos}/atrasados`)

    if (response.data) {
      exibirListaEmprestimos(response.data, "Empréstimos Atrasados")
      document.getElementById("resultadoBusca").style.display = "none"
    } else {
      showAlert("Nenhum empréstimo atrasado encontrado", "info")
    }
  } catch (error) {
    console.error("Erro ao listar empréstimos atrasados:", error)
    showAlert("Erro ao carregar empréstimos atrasados", "error")
  }
}

async function buscarEmprestimo() {
  const id = document.getElementById("buscarEmprestimoId").value

  if (!id) {
    showAlert("Digite o ID do empréstimo", "error")
    return
  }

  try {
    const response = await apiRequest(`${API_ENDPOINTS.emprestimos}/${id}`)

    if (response.data) {
      exibirDadosEmprestimo(response.data)
      document.getElementById("listaEmprestimos").style.display = "none"
    } else {
      showAlert("Empréstimo não encontrado", "error")
      document.getElementById("resultadoBusca").style.display = "none"
    }
  } catch (error) {
    console.error("Erro ao buscar empréstimo:", error)
    showAlert("Erro ao buscar empréstimo", "error")
    document.getElementById("resultadoBusca").style.display = "none"
  }
}

async function buscarEmprestimosPorCliente() {
  const clienteId = document.getElementById("buscarClienteId").value

  if (!clienteId) {
    showAlert("Digite o ID do cliente", "error")
    return
  }

  try {
    const response = await apiRequest(`${API_ENDPOINTS.emprestimos}/cliente/${clienteId}`)

    if (response.data && response.data.length > 0) {
      exibirListaEmprestimos(response.data, `Empréstimos do Cliente ${clienteId}`)
      document.getElementById("resultadoBusca").style.display = "none"
    } else {
      showAlert("Nenhum empréstimo encontrado para este cliente", "info")
    }
  } catch (error) {
    console.error("Erro ao buscar empréstimos por cliente:", error)
    showAlert("Erro ao buscar empréstimos do cliente", "error")
  }
}

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
            <td>${emprestimo.clienteId}</td>
            <td>${emprestimo.livroId}</td>
            <td>${formatDate(emprestimo.dataEmprestimo)}</td>
            <td>${formatDate(emprestimo.dataPrevistaDevolucao)}</td>
            <td>${emprestimo.dataDevolucao ? formatDate(emprestimo.dataDevolucao) : "-"}</td>
            <td><span class="status ${statusClass}">${emprestimo.status || "ATIVO"}</span></td>
            <td class="actions">
                ${!isDevolvido ? `<button class="btn btn-success btn-sm" onclick="registrarDevolucao(${emprestimo.id})">Devolver</button>` : ""}
                <button class="btn btn-danger btn-sm" onclick="excluirEmprestimo(${emprestimo.id})">Excluir</button>
            </td>
        `
  })

  container.style.display = "block"
}

function exibirDadosEmprestimo(emprestimo) {
  const container = document.getElementById("dadosEmprestimo")
  const isDevolvido = emprestimo.dataDevolucao

  container.innerHTML = `
        <div class="emprestimo-info">
            <p><strong>ID:</strong> ${emprestimo.id}</p>
            <p><strong>Cliente ID:</strong> ${emprestimo.clienteId}</p>
            <p><strong>Livro ID:</strong> ${emprestimo.livroId}</p>
            <p><strong>Data do Empréstimo:</strong> ${formatDate(emprestimo.dataEmprestimo)}</p>
            <p><strong>Data Prevista para Devolução:</strong> ${formatDate(emprestimo.dataPrevistaDevolucao)}</p>
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

async function registrarDevolucao(id) {
  if (!confirm("Confirma a devolução deste livro?")) {
    return
  }

  try {
    const response = await apiRequest(`${API_ENDPOINTS.emprestimos}/${id}/devolver`, {
      method: "PATCH",
    })

    if (response.data) {
      showAlert("Devolução registrada com sucesso!", "success")

      // Atualizar lista se estiver visível
      if (document.getElementById("listaEmprestimos").style.display !== "none") {
        listarTodosEmprestimos()
      }

      // Atualizar busca individual se for o mesmo empréstimo
      if (document.getElementById("resultadoBusca").style.display !== "none") {
        buscarEmprestimo()
      }
    } else if (response.error) {
      showAlert(response.error.message || "Erro ao registrar devolução", "error")
    }
  } catch (error) {
    console.error("Erro ao registrar devolução:", error)
    showAlert("Erro ao registrar devolução", "error")
  }
}

async function excluirEmprestimo(id) {
  if (!confirm("Tem certeza que deseja excluir este empréstimo?")) {
    return
  }

  try {
    const response = await fetch(`${API_ENDPOINTS.emprestimos}/${id}`, {
      method: "DELETE",
    })

    if (response.ok) {
      showAlert("Empréstimo excluído com sucesso!", "success")

      // Atualizar lista se estiver visível
      if (document.getElementById("listaEmprestimos").style.display !== "none") {
        listarTodosEmprestimos()
      }

      // Limpar busca individual se for o mesmo empréstimo
      if (document.getElementById("resultadoBusca").style.display !== "none") {
        document.getElementById("resultadoBusca").style.display = "none"
      }
    } else {
      showAlert("Erro ao excluir empréstimo", "error")
    }
  } catch (error) {
    console.error("Erro ao excluir empréstimo:", error)
    showAlert("Erro ao excluir empréstimo", "error")
  }
}

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
// Assuming these are defined elsewhere, but for the sake of a complete, runnable code, I'll add placeholders:
const API_ENDPOINTS = {
  emprestimos: "/api/emprestimos",
}

async function apiRequest(url, options = {}) {
  // Placeholder implementation
  console.log(`API Request: ${url}`, options)
  return { data: [], error: null } // Example response
}

function showAlert(message, type) {
  // Placeholder implementation
  alert(`${type}: ${message}`)
}

function formatDate(dateString) {
  // Placeholder implementation
  return dateString
}
